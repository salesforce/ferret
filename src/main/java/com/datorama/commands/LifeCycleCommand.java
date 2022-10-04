/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.exceptions.FerretException;
import com.datorama.models.LifeCycleEnum;
import com.datorama.services.GitRepositoryService;
import com.datorama.services.GlobalDirectoryService;
import com.datorama.services.LifeCycleService;
import com.datorama.services.pipelines.PipelineProvider;
import com.datorama.services.pipelines.PipelinesRepositoryService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class LifeCycleCommand implements FerretErrorHandler {
	private final GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();
	private final LifeCycleService lifeCycleService = LifeCycleService.getInstance();
	@CommandLine.Option(names = { "--file", "-f" }, description = "Yaml file.", paramLabel = "yaml file")
	private File yamlFile;
	@CommandLine.Option(names = { "--stage", "-s" }, description = "Stage to run.", defaultValue = "")
	private String stage;
	@CommandLine.Option(names = { "--pipeline", "-p" }, description = "pipeline from repository to run.")
	private String pipeline;
	@CommandLine.Option(names = { "--argument", "-a", "--arg" }, description = "arguments to pass to the pipeline to use. example: --argument key=value")
	private Map<String, String> arguments;

	void runLifeCycle(LifeCycleEnum lifeCycleEnum, LifeCycleCommand lifeCycleCommand) {
		ferretRun(() -> {
			globalDirectoryService.initialize();
			if (StringUtils.isEmpty(pipeline) && ObjectUtils.isEmpty(yamlFile)) {
				CommandLine commandLine = new CommandLine(lifeCycleCommand);
				commandLine.usage(System.out);
				return;
			}
			if (StringUtils.isNotEmpty(pipeline) && ObjectUtils.isNotEmpty(yamlFile)) {
				throw new FerretException("Run pipeline or file but not both.", CommandLine.ExitCode.USAGE);
			}
			lifeCycleService.setLifeCycleEnum(lifeCycleEnum);
			if (StringUtils.isNotEmpty(pipeline)) {
				GitRepositoryService.getInstance();
				PipelinesRepositoryService pipelinesRepositoryService = PipelinesRepositoryService.getInstance();
				Optional<PipelineProvider> pipelineProviderOptional = pipelinesRepositoryService.pipelines().stream().filter(pipelineProvider -> pipelineProvider.getKeyPrefix().equals(pipeline))
						.findFirst();
				if (pipelineProviderOptional.isPresent()) {
					lifeCycleService.runLifeCycleSectionInPipeline(pipelineProviderOptional.get().getPath().toFile(), stage, arguments);
					return;
				} else {
					throw new FerretException("Didn't find " + pipeline + " in the repository given. To list available pipelines, type: ferret pipelines", CommandLine.ExitCode.USAGE);
				}
			}
			if (ObjectUtils.isNotEmpty(yamlFile)) {
				GitRepositoryService.getInstance();
				lifeCycleService.runLifeCycleSectionInPipeline(yamlFile, stage, arguments);
			}
		});
	}
}
