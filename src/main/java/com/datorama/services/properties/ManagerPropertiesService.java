/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.LoggerFactory;

import com.datorama.exceptions.FerretException;
import com.datorama.services.ArgumentService;
import com.datorama.services.InputPropertyService;
import com.datorama.services.interfaces.PropertiesDirectory;
import com.datorama.services.interfaces.SpecialProperty;
import com.datorama.services.properties.directory.UserPropertiesService;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

public class ManagerPropertiesService {
	private static ManagerPropertiesService managerPropertiesService;
	private final Logger log = (Logger) LoggerFactory.getLogger(ManagerPropertiesService.class);
	private final InputPropertyService inputPropertyService = InputPropertyService.getInstance();
	private final SpecialProperties specialProperties = SpecialProperties.getInstance();
	private final DirectoryProperties directoryProperties = DirectoryProperties.getInstance();
	private final ArgumentService argumentService = ArgumentService.getInstance();

	private ManagerPropertiesService() {
		//Deny init
	}

	public static ManagerPropertiesService getInstance() {
		if (managerPropertiesService == null) {
			synchronized (ManagerPropertiesService.class) {
				if (managerPropertiesService == null) {
					managerPropertiesService = new ManagerPropertiesService();
				}
			}
		}
		return managerPropertiesService;
	}

	private Optional<String> getPropertyForRewrite(String propertyKey, Path currentFilePath) {
		Optional<String> directoryPropertyResult = searchInDirectoryProperties(propertyKey, currentFilePath);
		if (directoryPropertyResult.isPresent()) {
			return directoryPropertyResult;
		}
		Optional<String> specialPropertyResult = searchInSpecialProperties(propertyKey, currentFilePath);
		if (specialPropertyResult.isPresent()) {
			return specialPropertyResult;
		}
		return Optional.empty();
	}

	public Map<String, String> getPropertiesToRewrite(List<String> keys, Path currentFilePath) {
		Map<String, String> map = new HashMap<>();
		keys.forEach(key -> {
			if (!denyRewriteOfSpecialProperties(key)) {
				Optional<String> value = getPropertyForRewrite(key, currentFilePath);
				if (value.isPresent()) {
					map.put(key, value.get());
				} else {
					throw new FerretException("Did not find value for key " + key, CommandLine.ExitCode.USAGE);
				}
			}
		});
		return map;
	}

	private boolean denyRewriteOfSpecialProperties(String propertyKey) {
		String prefixOfKey = StringUtils.substring(propertyKey, 0, inputPropertyService.getPropertyKey().length());
		if (prefixOfKey.equals(inputPropertyService.getPropertyKey())) {
			log.debug("an input property: " + propertyKey);
			return true;
		}
		return false;
	}

	private Optional<String> searchInSpecialProperties(String propertyKey, Path currentFilePath) {
		Optional<SpecialProperty> specialPropertyOptional = specialProperties.getSpecialProperties()
				.stream()
				.filter(specialProperty -> propertyKey.equals(specialProperty.getPropertyKey()))
				.findFirst();
		if (!specialPropertyOptional.isPresent()) {
			return Optional.empty();
		}
		log.debug("Found property " + specialPropertyOptional.get().getPropertyKey() + " in special property.");
		return Optional.of(specialPropertyOptional.get().getPropertyValue(currentFilePath));
	}

	private Optional<String> searchInDirectoryProperties(String propertyKey, Path currentFilePath) {
		Optional<PropertiesDirectory> propertiesDirectoryOptional = directoryProperties.getPropertiesDirectories().stream().filter(it -> {
			String prefixOfProperty = StringUtils.substring(propertyKey, 0, it.getKeyPrefix().length());
			return prefixOfProperty.equals(it.getKeyPrefix());
		}).findFirst();
		if (!propertiesDirectoryOptional.isPresent()) {
			return Optional.empty();
		}
		PropertiesDirectory propertiesDirectory = propertiesDirectoryOptional.get();
		String propertyKeyRealName = propertyKey.substring(propertiesDirectory.getKeyPrefix().length());
		String valueOfProperty = propertiesDirectoryOptional.get().getProperties(currentFilePath).getProperty(propertyKeyRealName);
		if (valueOfProperty == null) {
			if (propertiesDirectory.getFileName().equals(UserPropertiesService.getInstance().getFileName())) {
				throw new FerretException("Searched " + propertyKeyRealName
								+ " in user properties (properties that are unique to the user) but did not find it. please add it with by typing: ferret user --property " + propertyKeyRealName + "=value",
						CommandLine.ExitCode.USAGE);
			}
			throw new FerretException("Searched " + propertyKeyRealName + " in properties file called " + propertiesDirectory.getFileName() + " but did not found the key. add it with the correct value",
					CommandLine.ExitCode.USAGE);
		}
		log.debug("Found property " + propertyKeyRealName + " in directory " + propertiesDirectory.getFileName());
		return Optional.of(propertiesDirectoryOptional.get().getProperties(currentFilePath).getProperty(propertyKeyRealName));
	}
}
