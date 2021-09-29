/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.codehaus.plexus.util.FileUtils;
import org.slf4j.LoggerFactory;

import com.datorama.common.Constants;
import com.datorama.exceptions.FerretException;
import com.datorama.services.properties.directory.UserPropertiesService;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;
import static com.datorama.common.Constants.*;

/**
 * Manage ferret hidden directory and global.
 */
public class GlobalDirectoryService {
	private static GlobalDirectoryService globalDirectoryService;
	private final Logger log = (Logger) LoggerFactory.getLogger(GlobalDirectoryService.class);
	private final CredentialsService credentialsService = CredentialsService.getInstance();

	private GlobalDirectoryService() {
		//Deny init
	}

	public static GlobalDirectoryService getInstance() {
		if (globalDirectoryService == null) {
			synchronized (GlobalDirectoryService.class) {
				if (globalDirectoryService == null) {
					globalDirectoryService = new GlobalDirectoryService();
					globalDirectoryService.initialize();
				}
			}
		}
		return globalDirectoryService;
	}

	public void initialize() {
		if (!Files.exists(FERRET_DIR)) {
			log.debug("Initialize main ferret directory at " + FERRET_DIR);
			try {
				Files.createDirectory(FERRET_DIR);

				credentialsService.createFile();
				UserPropertiesService userPropertiesService = UserPropertiesService.getInstance();
				userPropertiesService.createDefaultProperties();
			} catch (IOException e) {
				log.warn("Failed initializing .ferret directory", e);
				throw new FerretException("Failed initializing .ferret directory.", CommandLine.ExitCode.SOFTWARE);
			}
		}
	}

	public File fileInTempDirectory(String fileName) {
		initialize();
		createDirectoryInFerretDir(Constants.FERRET_TEMP_DIR);
		Path filePath = Paths.get(Constants.FERRET_TEMP_DIR.toString(), fileName);
		if (!Files.exists(filePath)) {
			log.debug("Creating temp file at " + filePath.toString());
			try {
				Files.createFile(filePath);
			} catch (IOException e) {
				log.warn("Failed creating temp file", e);
				throw new FerretException("Failed creating temp file.", CommandLine.ExitCode.SOFTWARE);
			}
		}
		return filePath.toFile();
	}

	public void createFileInSystem(Path file) {
		initialize();
		createDirectoryInFerretDir(file.getParent());
		createFile(file);
	}

	public void createDirectoryInFerretDir(Path path) {
		{
			if (!Files.exists(path)) {
				log.debug("Creating directory at " + path.toString());
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
					log.warn("Failed creating directory", e);
					throw new FerretException("Failed creating directory.", CommandLine.ExitCode.SOFTWARE);
				}
			}
		}
	}

	private File createFile(Path fileDir) {
		log.debug("Creating file at " + fileDir.toString());
		File file = new File(fileDir.toString());
		file.setReadable(true);
		file.setWritable(true);
		file.setExecutable(true);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log.warn("Failed creating file", e);
			throw new FerretException("Failed creating file.", CommandLine.ExitCode.SOFTWARE);
		}
		return file;
	}

	public void deleteSubFolder() {
		try {
			OutputService.getInstance().normal("deleting.ferret/remote folder");
			FileUtils.deleteDirectory(new File(REMOTE_DIR.toString()));
			OutputService.getInstance().normal("deleting.ferret/tmp folder");
			FileUtils.deleteDirectory(new File(FERRET_TEMP_DIR.toString()));
		} catch (IOException e) {
			log.warn("Failed deleting .ferret/remote or .ferret/temp folder", e);
			throw new FerretException("Failed deleting .ferret folder", CommandLine.ExitCode.SOFTWARE);
		}
	}
}
