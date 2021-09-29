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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.datorama.exceptions.FerretException;
import com.datorama.files.FerretYamlFile;
import com.datorama.models.Apply;
import com.datorama.models.Argument;
import com.datorama.services.interfaces.SpecialProperty;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

public class ArgumentService implements SpecialProperty {
	private static ArgumentService argumentService;
	private final Logger log = (Logger) LoggerFactory.getLogger(ArgumentService.class);

	private ArgumentService() {
		//Deny init
	}

	public static ArgumentService getInstance() {
		if (argumentService == null) {
			synchronized (ArgumentService.class) {
				if (argumentService == null) {
					argumentService = new ArgumentService();
				}
			}
		}
		return argumentService;
	}

	/**
	 * List of arguments to be used in other pipeline by injecting them (applying of pipeline to use)
	 * @param apply
	 * @return
	 */
	public Map<String, String> listFromApplyToMap(Apply apply) throws FerretException {
		validateListArgumentInjected(apply.getArguments());
		Map<String, String> map = new HashMap<>();
		apply.getArguments().forEach(argument -> {
			String value;
			if (StringUtils.isNotEmpty(argument.getValue())) {
				value = argument.getValue();
			} else {
				value = argument.getDefaultValue();
			}
			map.put(argument.getKey(), value);
		});
		return map;
	}

	private void validateListArgumentInjected(List<Argument> argumentList) throws FerretException {
		for (Argument argument : argumentList) {
			if (StringUtils.isEmpty(argument.getKey())) {
				throw new FerretException("argument must have valid key (not empty).", CommandLine.ExitCode.USAGE);
			}
			if (StringUtils.isNotEmpty(argument.getDescription())) {
				throw new FerretException("argument to be used in other pipeline does not need description.", CommandLine.ExitCode.USAGE);
			}
			if (StringUtils.isNotEmpty(argument.getDefaultValue())) {
				throw new FerretException("argument to be used in other pipeline does not need default value.", CommandLine.ExitCode.USAGE);
			}
		}
	}

	@Override public String getPropertyKey() {
		return "argument.";
	}

	@Override public String getPropertyValue(Path currentPath) {
		throw new NotImplementedException();
	}

	/**
	 * Get pipeline arguments with injected from outside
	 * @param argumentsFromYaml
	 * @param argumentsInjectFromOutside
	 * @return
	 */
	public Map<String,String> getPipelineArgumentsToRewrite(List<Argument> argumentsFromYaml, Map<String, String> argumentsInjectFromOutside) {
		Map<String,String> argumentsMap = new HashMap<>();
		argumentsFromYaml.forEach(argument -> {
			if(ObjectUtils.isEmpty(argumentsInjectFromOutside) ||!argumentsInjectFromOutside.containsKey(argument.getKey())){
				argumentsMap.put(getPropertyKey() + argument.getKey(), argument.getDefaultValue());
			} else {
				argumentsMap.put(getPropertyKey() + argument.getKey(), argumentsInjectFromOutside.get(argument.getKey()));
			}
		});
		return argumentsMap;
	}

	public void printPipelineArguments(File file,String emptyMessage) throws FerretException {
		FerretYamlFile ferretYamlFile = (FerretYamlFile) YamlFileService.loadYamlAs(file, FerretYamlFile.class);
		final StringBuilder stringBuilder = new StringBuilder();
		if (ferretYamlFile.getArguments().isEmpty()) {
			stringBuilder.append(emptyMessage);
			OutputService.getInstance().normal(OutputService.getInstance().boldPart(stringBuilder.toString()));
			return;
		}
		stringBuilder.append("------------------------------").append(System.lineSeparator());
		ferretYamlFile.getArguments().forEach(argument -> {
			stringBuilder.append("Key: ").append(argument.getKey()).append(System.lineSeparator());
			stringBuilder.append("Description: ").append(argument.getDescription()).append(System.lineSeparator());
			stringBuilder.append("Default-Value: ").append(argument.getDefaultValue()).append(System.lineSeparator());
			stringBuilder.append("------------------------------");
		});
		OutputService.getInstance().normal(stringBuilder.toString());
	}
}
