/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.exceptions.FerretException;
import com.datorama.models.Input;
import com.datorama.services.interfaces.SpecialProperty;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.Optional;

public class InputPropertyService implements SpecialProperty {
	private static final Logger log = Logger.getLogger(InputPropertyService.class);
	private static InputPropertyService inputPropertyService;
	private final ProcessService processService = ProcessService.getInstance();
	private final RemoteService remoteService = RemoteService.getInstance();

	private InputPropertyService() {
		//Deny init
	}

	public static InputPropertyService getInstance() {
		if (inputPropertyService == null) {
			synchronized (InputPropertyService.class) {
				if (inputPropertyService == null) {
					inputPropertyService = new InputPropertyService();
				}
			}
		}
		return inputPropertyService;
	}

	public String getValueFromInput(Input input, Path currentDirectory) {
		if (StringUtils.isNotEmpty(input.getRequest()) && StringUtils.isNotEmpty(input.getCommand()) && ObjectUtils.isNotEmpty(input.getRemote())) {
			throw new FerretException("Failed because you can run only one type of input.", CommandLine.ExitCode.USAGE);
		}
		if (StringUtils.isNotEmpty(input.getRequest())) {
			return valueFromRequest(input);
		}
		if (StringUtils.isNotEmpty(input.getCommand())) {
			return valueFromRun(input, currentDirectory);
		}
		if (ObjectUtils.isNotEmpty(input.getRemote())) {
			return remoteService.remoteFile(input.getRemote()).getAbsolutePath();
		}
		return "";
	}

	private String valueFromRun(Input input, Path currentDirectory) {
		Optional<String> resultOptional = processService.runCommandWithResult(input.getCommand(), currentDirectory);
		if (resultOptional.isPresent()) {
			return resultOptional.get().trim();
		} else {
			log.warn("Was not suppose to be able to reach here.");
			return "";
		}
	}

	private String valueFromRequest(Input input) {
		ConsoleMessageService.sendInputRequestMessage(input);
		String result = ConsoleInputService.getUserInput();
		if (result.isEmpty()) {
			return input.getDefaultValue();
		} else {
			return result;
		}
	}

	@Override public String getPropertyKey() {
		return "input.";
	}

	@Override public String getPropertyValue(Path currentPath) {
		throw new NotImplementedException();
	}
}
