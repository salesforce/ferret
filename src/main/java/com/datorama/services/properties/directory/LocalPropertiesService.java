/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties.directory;

import com.datorama.common.Constants;
import com.datorama.exceptions.FerretException;
import com.datorama.services.interfaces.PropertiesDirectory;
import com.datorama.services.properties.FilePropertiesService;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

/**
 * Currently not supported will support if there is usage.
 */
public class LocalPropertiesService implements PropertiesDirectory {
	private static final Logger log = Logger.getLogger(LocalPropertiesService.class);

	@Override public String getFileName() {
		return "local.properties";
	}

	@Override public String getKeyPrefix() {
		return "local.";
	}

	@Override public File getFile(Path currentFilePath) {
		Path fullPath = currentFilePath.toAbsolutePath();
		Path currentDirectory = fullPath.getParent();
		File localPropertiesFile = null;
		boolean run = true;
		try {
			while (run && currentDirectory != null) {
				Optional<Path> optionalPath = Files.list(currentDirectory).filter(path -> {
					String fileName = path.getFileName().toString();
					return fileName.equals(Constants.FERRET_DIR_NAME) || fileName.equals(getFileName());
				}).findFirst();
				if (optionalPath.isPresent()) {
					Path path = optionalPath.get();
					if (path.getFileName().toString().equals(Constants.FERRET_DIR_NAME)) {
						Path filePath = Paths.get(path.toString(), getFileName());
						if (!Files.exists(filePath)) {
							throw new FerretException("Didn't find " + getFileName() + " in " + path.toAbsolutePath().toString(), CommandLine.ExitCode.USAGE);
						}
						localPropertiesFile = filePath.toFile();
					} else {
						localPropertiesFile = path.toFile();
					}
					run = false;
				}
				currentDirectory = currentDirectory.getParent();
				if (currentDirectory.toAbsolutePath().toString().equals(Constants.USER_HOME_PATH)) {
					run = false;
					log.debug("Reached " + Constants.USER_HOME_PATH + ", stopped searching.");
				}
			}
		} catch (IOException e) {
			log.warn("IO error.", e);
			throw new FerretException(String.format("Error in searching for %s, error -> %s", getFileName(), e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		}
		if (localPropertiesFile == null) {
			throw new FerretException(String.format("Did not find %s after searching all directories in the path of %s.", getFileName(), currentFilePath.toAbsolutePath()), CommandLine.ExitCode.USAGE);
		}
		return localPropertiesFile;
	}

	@Override public Properties getProperties(Path currentFilePath) {
		return FilePropertiesService.readProperties(getFile(currentFilePath));
	}
}
