/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.datorama.exceptions.FerretException;
import com.datorama.files.FerretYamlFile;
import com.datorama.models.*;
import com.datorama.services.pipelines.PipelineProvider;
import com.datorama.services.pipelines.PipelinesRepositoryService;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;
import static com.datorama.common.Constants.USER_HOME_PATH;

/**
 * something to think about, if 2 terminals open and run ferret, it mean 2 processes or same process. if the same it affect application design. (will need to add flyweight for each singleton, to include unique caller)
 */
public class LifeCycleService {
	private final ReplaceKeysService replaceKeysService = ReplaceKeysService.getInstance();
	private final ProcessService processService = ProcessService.getInstance();
	private final InputPropertyService inputPropertyService = InputPropertyService.getInstance();
	private final RewriteYamlService rewriteYamlService = RewriteYamlService.getInstance();
	private PropertiesConfigurationInYamlService propertiesConfigurationInYamlService = PropertiesConfigurationInYamlService.getInstance();
	private  RemoteService remoteService = RemoteService.getInstance();
	private WhenService whenService = WhenService.getInstance();
	private static LifeCycleService lifeCycleService;
	private final Logger log = (Logger) LoggerFactory.getLogger(LifeCycleService.class);
	private LifeCycleEnum lifeCycleEnum;
	private ArgumentService argumentService = ArgumentService.getInstance();
	private FailureService failureService = FailureService.getInstance();
	private SummaryService summaryService = SummaryService.getInstance();
	private IntroductionService introductionService = IntroductionService.getInstance();

	private LifeCycleService() {
		//Deny init
	}

	public static LifeCycleService getInstance() {
		if (lifeCycleService == null) {
			synchronized (LifeCycleService.class) {
				if (lifeCycleService == null) {
					lifeCycleService = new LifeCycleService();
				}
			}
		}
		return lifeCycleService;
	}

	private LifeCycleEnum getLifeCycleEnum() {
		if (ObjectUtils.isEmpty(lifeCycleEnum)) {
			throw new IllegalArgumentException("Must set the lifecycle to be run");
		}
		return lifeCycleEnum;
	}

	public void setLifeCycleEnum(LifeCycleEnum lifeCycleEnum) {
		this.lifeCycleEnum = lifeCycleEnum;
	}

	public void runLifeCycleSectionInPipeline(File yamlFile, String stageName,Map<String,String> argumentsInjectFromOutside) throws FerretException {
		FerretYamlFile ferretYamlFile = (FerretYamlFile) YamlFileService.loadYamlAs(yamlFile, FerretYamlFile.class);
		introductionService.printIntroduction(ferretYamlFile.getIntroduction());
		propertiesConfigurationInYamlService.runUserInteraction(ferretYamlFile.getProperties());
		Map<String,String> argumentsMapToRewrite = argumentService.getPipelineArgumentsToRewrite(ferretYamlFile.getArguments(),argumentsInjectFromOutside);
		FerretYamlFile rewrittenFile = rewriteYamlService.rewriteFerretYamlFile(yamlFile,argumentsMapToRewrite);
		Path fileDirectory = FileService.getCurrentDirectory(rewrittenFile.getDirectory(), Paths.get(USER_HOME_PATH));
		Map<String, String> inputsMap = new HashMap<>();
		fillInputMap(inputsMap, rewrittenFile.getInputs(),fileDirectory);
		if (ObjectUtils.isNotEmpty(ferretYamlFile.getWhen())) {
			if (!whenService.evaluate(ferretYamlFile.getWhen(), fileDirectory)) {
				return;
			}
		}
		if (StringUtils.isEmpty(stageName)) {
			summaryService.setStartTime();
			rewrittenFile.getStages().forEach((name, stageAtt) -> {
				Map<String, String> mainInputToCorrupt = inputsMap;
				inStage(fileDirectory, name, stageAtt, mainInputToCorrupt);
			});
		} else {
			StageAttributes stageAttributes = rewrittenFile.getStages().get(stageName);
			if (stageAttributes == null) {
				throw new FerretException(String.format("%s stage does not exist in %s", stageName, yamlFile.toPath().toAbsolutePath()), CommandLine.ExitCode.USAGE);
			}
			summaryService.setStartTime();
			Map<String, String> mainInputToCorrupt = inputsMap;
			inStage(fileDirectory, stageName, stageAttributes, mainInputToCorrupt);
		}
	}


	private void inStage(Path fileDirectory, String stageName, StageAttributes stageAtt, Map<String, String> mapToCorrupt) {
		failureService.setFailure(stageAtt.getOnFailure());
		Path stageDirectory = FileService.getCurrentDirectory(stageAtt.getDirectory(), fileDirectory);
		boolean toRun = evaluateWhen(stageAtt.getWhen(), stageDirectory,stageName,stageAtt);
		if(toRun){
			List<Command> commandsToExecute = getCommandList(stageAtt);
			if(!commandsToExecute.isEmpty()){
				OutputService.getInstance().header1(String.format("Running stage: %s, description: %s.", stageName, stageAtt.getDescription()));
				fillInputMap(mapToCorrupt, stageAtt.getInputs(),stageDirectory);
				SummaryStage summaryStage = summaryService.startOfStage(stageAtt.getSummary(),stageName,mapToCorrupt);
                commandsToExecute.forEach(command -> execute(stageDirectory, command, mapToCorrupt));
				summaryService.endOfStage(summaryStage);
			}
		}
	}

	private List<Command> getCommandList(StageAttributes stageAtt) {
		switch (getLifeCycleEnum()) {
			case SETUP:
				return stageAtt.getSetup();
			case TEARDOWN:
				return stageAtt.getTeardown();
			default:
				throw new IllegalArgumentException("Illegal argument received: " + getLifeCycleEnum());
		}
	}

	private boolean evaluateWhen(When when, Path stageDirectory,String stageName,StageAttributes stageAttributes) {
		boolean toRun = false;
		try {
			toRun = whenService.evaluate(when,stageDirectory);
		} catch (FerretException e) {
			final StringBuilder sb = new StringBuilder();
			sb.append(e.getMessage()).append(System.lineSeparator());
			sb.append("The failure happened in stage ").append(stageName).append(".");
			if(StringUtils.isNotEmpty(stageAttributes.getDescription())){
				sb.append(" Description: ").append(stageAttributes.getDescription());
			}
			throw new FerretException(sb.toString(),e.getExitCode());
		}
		return toRun;
	}

	private void executeCommandLine(Path stageDirectory, Command command, Map<String, String> inputMap) {
		Path commandDirectory = FileService.getCurrentDirectory(command.getDirectory(), stageDirectory);
		String commandValue = replaceKeysService.replaceKeysInLine(command.getCommand(), inputMap);
		OutputService.getInstance().header2(String.format("Running command: %s, working directory: %s.", commandValue, commandDirectory.toString()));
		processService.runCommandWithoutResult(commandValue, commandDirectory);
	}

	private void executeApply(Apply apply) throws FerretException {
		if(StringUtils.isEmpty(apply.getFile()) && ObjectUtils.isEmpty(apply.getRemote()) && StringUtils.isEmpty(apply.getPipeline())){
			throw new FerretException("Failed because file/remote/pipeline fields are empty, they are needed for applying other ferret pipeline.", CommandLine.ExitCode.USAGE);
		}
		if(StringUtils.isNoneEmpty(apply.getFile(),apply.getPipeline()) && ObjectUtils.isNotEmpty(apply.getRemote())){
			throw new FerretException("Failed because ferret can only apply a file or a remote or a pipeline.", CommandLine.ExitCode.USAGE);
		}
		File appliedFile = null;
		//pull and add all from common repo
		GitRepositoryService.getInstance();
		if(StringUtils.isNotEmpty(apply.getFile())){
			OutputService.getInstance().header1("Applying yaml file: " + apply.getFile());
			appliedFile = new File(apply.getFile());
			runLifeCycleSectionInPipeline(appliedFile, apply.getStage(),argumentService.listFromApplyToMap(apply));
			return;
		}
		if (ObjectUtils.isNotEmpty(apply.getRemote())) {
			OutputService.getInstance().header1("Applying remote: " + apply.getRemote());
			String suffix = apply.getRemote().getFileNameSuffix();
			if(!StringUtils.equalsAnyIgnoreCase(suffix,"yaml","yml")){
				throw new FerretException("Ferret pipeline only support YAML file type. not file type: " + suffix, CommandLine.ExitCode.USAGE);
			}
			appliedFile = remoteService.remoteFile(apply.getRemote());
			runLifeCycleSectionInPipeline(appliedFile, apply.getStage(),argumentService.listFromApplyToMap(apply));
			return;
		}

		if(StringUtils.isNotEmpty(apply.getPipeline())){
			PipelinesRepositoryService pipelinesRepositoryService = PipelinesRepositoryService.getInstance();
			Optional<PipelineProvider> pipelineProviderOptional = pipelinesRepositoryService.pipelines().stream().filter(pipelineProvider -> pipelineProvider.getKeyPrefix().equals(apply.getPipeline()))
					.findFirst();
			if (pipelineProviderOptional.isPresent()) {
				OutputService.getInstance().header1("Applying pipeline: " + apply.getPipeline());
				appliedFile = pipelineProviderOptional.get().getPath().toFile();
				runLifeCycleSectionInPipeline(appliedFile, apply.getStage(),argumentService.listFromApplyToMap(apply));
				return;
			} else {
				throw new FerretException("Didn't find " + apply.getPipeline() + " in the repository given. To list available pipelines:, type: ferret pipelines", CommandLine.ExitCode.USAGE);
			}
		}
	}

	private void execute(Path stageDirectory, Command command, Map<String, String> inputMap) {
		if(StringUtils.isNotEmpty(command.getCommand()) && ObjectUtils.isNotEmpty(command.getApply())){
			throw new FerretException("Failed because you can run only one type of command. apply or commands.", CommandLine.ExitCode.USAGE);
		}
		if(StringUtils.isNotEmpty(command.getCommand())){
			executeCommandLine(stageDirectory, command, inputMap);
			return;
		}
		if(ObjectUtils.isNotEmpty(command.getApply())){
			try {
				executeApply(command.getApply());
			} catch (FerretException e) {
				throw new FerretException(e.getMessage(),e.getExitCode());
			}
			return;
		}
	}

	private void fillInputMap(Map<String, String> inputMap, List<Input> inputs,Path directory) {
		if (inputs != null) {
			inputs.forEach(input -> {
				Input stageInput = input;
				String valueMain = inputPropertyService.getValueFromInput(stageInput,directory);
				inputMap.put(inputPropertyService.getPropertyKey() + stageInput.getKey(), valueMain);
			});
		}
	}
}
