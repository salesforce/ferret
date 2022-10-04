/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.common.Constants;
import com.datorama.exceptions.FerretException;
import com.datorama.models.ProcessResponse;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.logging.Logger;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Process command and run it.
 */
public class ProcessService {
	private static ProcessService processService;
	private GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();
	private static final Logger log = Logger.getLogger(ProcessService.class);

	private ProcessService() {
		//Deny init
	}

	public static ProcessService getInstance() {
		if (processService == null) {
			synchronized (ProcessService.class) {
				if (processService == null) {
					processService = new ProcessService();
					processService.initialize();
				}
			}
		}
		return processService;
	}

	private void initialize(){
		globalDirectoryService.createDirectoryInFerretDir(Constants.FERRET_TEMP_SCRIPTS_DIR);
		FileService.deleteOnlyFilesInDirectory(Constants.FERRET_TEMP_SCRIPTS_DIR);
	}

	public void runCommandWithoutResult(String command, Path directory) {
		Process process = null;
		ProcessBuilder processBuilder = null;
		try {
			File file = FileService.createExecutableScriptFile(command);
			List<String> list = arrayCommand(file);
			processBuilder = defaultProcessBuilder(list, directory);
			process = processBuilder.start();
			int exitCode = process.waitFor();
			if (exitCode != CommandLine.ExitCode.OK) {
				throw new FerretException(String.format("Failed in running -> %s", command), CommandLine.ExitCode.USAGE);
			}
		} catch (IOException e) {
			log.warn("IO error.", e);
			if (e.getMessage() != null && e.getMessage().contains("or directory")) {
				throw new FerretException(String.format("Cannot find directory \"%s\". tip: it might because you using absolute path unnecessarily.", processBuilder.directory().toString()),
						CommandLine.ExitCode.SOFTWARE);
			} else {
				throw new FerretException(String.format("Input/Output error. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
			}
		} catch (InterruptedException e) {
			log.warn("Process error.", e);
			throw new FerretException(String.format("Process interruption. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} finally {
			if (process != null && process.isAlive()) {
				process.destroy();
			}
		}
	}

	private List<String> arrayCommand(File file) {
		List<String> list = new ArrayList();
		if (SystemUtils.IS_OS_WINDOWS) {
			throw new UnsupportedOperationException("Did not implement for windows.");
		} else {
			list.add(file.getAbsolutePath());
		}
		return list;
	}

	public Optional<String> runCommandWithResult(String command, Path directory) {
		try {
			ProcessResult processResult = processExtractor(command, directory);
			if (processResult.getExitValue() != CommandLine.ExitCode.OK) {
				throw new FerretException(String.format("Failed in running -> %s", command), CommandLine.ExitCode.USAGE);
			}
			String result = processResult.outputUTF8().trim();
			log.debug("Command result value is " + result);
			return Optional.of(result);
		} catch (IOException e) {
			log.warn("Input/Output error.", e);
			throw new FerretException(String.format("Input/Output. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (InterruptedException e) {
			log.warn("Thread interruption error.", e);
			throw new FerretException(String.format("Thread interruption. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (TimeoutException e) {
			log.warn("Process timeout error.", e);
			throw new FerretException(String.format("Process timeout. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		}
	}

	public Optional<Integer> runCommandExitCode(String command, Path directory) {
		try {
			ProcessResult processResult = processExtractor(command, directory);
			int exitValue = processResult.getExitValue();
			log.debug("Command result with exit code of " + exitValue);
			return Optional.of(exitValue);
		} catch (IOException e) {
			log.warn("Input/Output error.", e);
			throw new FerretException(String.format("Input/Output. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (InterruptedException e) {
			log.warn("Thread interruption error.", e);
			throw new FerretException(String.format("Thread interruption. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (TimeoutException e) {
			log.warn("Process timeout error.", e);
			throw new FerretException(String.format("Process timeout. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		}
	}

	public Optional<ProcessResponse> runCommand(String command, Path directory) {
		try {
			ProcessResult processResult = processExtractor(command, directory);
			int exitValue = processResult.getExitValue();
			Optional<String> output = Optional.of(processResult.outputUTF8().trim());
			ProcessResponse processResponse = new ProcessResponse();
			processResponse.setExitCode(exitValue);
			processResponse.setOutput(output);
			return Optional.of(processResponse);
		} catch (IOException e) {
			log.warn("Input/Output error.", e);
			throw new FerretException(String.format("Input/Output. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (InterruptedException e) {
			log.warn("Thread interruption error.", e);
			throw new FerretException(String.format("Thread interruption. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (TimeoutException e) {
			log.warn("Process timeout error.", e);
			throw new FerretException(String.format("Process timeout. message: %s", e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		}
	}

	private ProcessResult processExtractor(String command, Path directory) throws IOException, InterruptedException, TimeoutException {
		ProcessExecutor processExecutor = new ProcessExecutor();
		File file = FileService.createExecutableScriptFile(command);
		List<String> list = arrayCommand(file);
		processExecutor.directory(directory.toFile()).command(list).readOutput(true);
		return processExecutor.execute();
	}

	private ProcessBuilder defaultProcessBuilder(List<String> command, Path directory) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(command);
		processBuilder.directory(directory.toFile());
		processBuilder.inheritIO();
		return processBuilder;
	}

}
