/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties.directory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import com.datorama.common.Constants;
import com.datorama.services.GlobalDirectoryService;
import com.datorama.services.interfaces.PropertiesDirectory;
import com.datorama.services.properties.FilePropertiesService;

import ch.qos.logback.classic.Logger;

public class UserPropertiesService implements PropertiesDirectory {
	private static UserPropertiesService userPropertiesService;
	private final Logger log = (Logger) LoggerFactory.getLogger(UserPropertiesService.class);

	private UserPropertiesService() {
		//Deny init
	}

	public static UserPropertiesService getInstance() {
		if (userPropertiesService == null) {
			synchronized (UserPropertiesService.class) {
				if (userPropertiesService == null) {
					userPropertiesService = new UserPropertiesService();
				}
			}
		}
		return userPropertiesService;
	}

	@Override public String getFileName() {
		return "user.properties";
	}

	@Override public String getKeyPrefix() {
		return "user.";
	}

	@Override public File getFile(Path currentFilePath) {
		return Paths.get(Constants.FERRET_DIR.toString(), getFileName()).toFile();
	}

	@Override public Properties getProperties(Path currentFilePath) {
		return FilePropertiesService.readProperties(getFile(null));
	}

	public void createDefaultProperties() {
		if (!Files.exists(getFile(null).toPath())) {
			GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();
			globalDirectoryService.createFileInSystem(getFile(null).toPath());
			Properties properties = new Properties();
			properties.setProperty("home", System.getProperty("user.home"));
			FilePropertiesService.writeProperties(getFile(null), properties);
		}
	}

	public void setProperties(Map<String, String> mapOfProperties) {
		createDefaultProperties();
		Properties userProperties = FilePropertiesService.readProperties(getFile(null));
		mapOfProperties.forEach((key, value) -> userProperties.setProperty(key, value));
		FilePropertiesService.writeProperties(getFile(null), userProperties);
	}
}
