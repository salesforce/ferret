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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class FileService {
	private static final Logger log = Logger.getLogger(FileService.class);

	public static Path getCurrentDirectory(String newDirectory, Path currentDirectory) {
		//		log.debug("new directory: " + newDirectory + " current directory: " + currentDirectory.toString());
		if (StringUtils.isEmpty(newDirectory)) {
			return currentDirectory;
		}
		Path dirPath = Paths.get(newDirectory);
		if (dirPath.isAbsolute()) {
			log.debug("absolute path: " + dirPath.toString());
			makeDirs(dirPath);
			return dirPath;
		} else {
			log.debug("relative path: " + dirPath.toString());
			Path newPath = Paths.get(currentDirectory.toString(), dirPath.toString());
			makeDirs(newPath);
			return newPath;
		}
	}

	private static void makeDirs(Path dirPath) {
		if (!dirPath.toFile().exists()) {
			log.debug("creates directories for path " + dirPath.toAbsolutePath());
			dirPath.toFile().mkdirs();
		}
	}

	public static File createExecutableScriptFile(String command) throws IOException {
		File file;
		if (SystemUtils.IS_OS_WINDOWS) {
			throw new UnsupportedOperationException("Did not implement for windows.");
		} else {
			file = File.createTempFile("ferret_script", ".sh", Constants.FERRET_TEMP_SCRIPTS_DIR.toFile());
			file.setExecutable(true);
			file.setWritable(true);
			file.setReadable(true);
			file.deleteOnExit();
			Files.write(file.toPath(), commandInShellFile(command).getBytes());
		}
		return file;
	}

	private static String commandInShellFile(String value) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("#!/bin/bash").append(System.lineSeparator());
		stringBuilder.append(value).append(System.lineSeparator());
		return stringBuilder.toString();
	}

	private static String commandInBatchFile(String value) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(value).append(System.lineSeparator());
		return stringBuilder.toString();
	}

	public static void isFileExist(File file) {
		if (!(file.exists() && file.isFile())) {
			throw new FerretException("Could not find file: " + file.getAbsolutePath(), CommandLine.ExitCode.USAGE);
		}
	}

	public static void deleteDirectoryAndAllFilesInside(Path directoryPath) {
		log.debug("Deleting " + directoryPath.toAbsolutePath().toString() + " and all it's content.");
		try {
			Files.walk(directoryPath)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
		} catch (IOException ioException) {
			throw new FerretException("Failed deleting the repository directory after failing to clone, delete it manually in path: " + Constants.GIT_REPO_DIR.toAbsolutePath().toString(),
					CommandLine.ExitCode.SOFTWARE);
		}
	}

	public static void deleteFilesAndSubdirectoriesInDirectory(Path directoryPath) {
		for (File file : directoryPath.toFile().listFiles()) {
			if (file.isDirectory()) {
				deleteFilesAndSubdirectoriesInDirectory(file.toPath());
			}
			file.delete();
		}
	}

	public static void deleteOnlyFilesInDirectory(Path directoryPath) {
		for (File file : directoryPath.toFile().listFiles())
			if (!file.isDirectory()) {
				file.delete();
			}
	}
}
