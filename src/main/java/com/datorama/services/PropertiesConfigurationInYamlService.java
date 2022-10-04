/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.exceptions.FerretException;
import com.datorama.models.YamlProperties;
import com.datorama.services.properties.directory.UserPropertiesService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfigurationInYamlService {
	private static PropertiesConfigurationInYamlService propertiesConfigurationInYamlService;
	private static final Logger log = Logger.getLogger(PropertiesConfigurationInYamlService.class);
	private final ProcessService processService = ProcessService.getInstance();

	private PropertiesConfigurationInYamlService() {
		//Deny init
	}

	public static PropertiesConfigurationInYamlService getInstance() {
		if (propertiesConfigurationInYamlService == null) {
			synchronized (PropertiesConfigurationInYamlService.class) {
				if (propertiesConfigurationInYamlService == null) {
					propertiesConfigurationInYamlService = new PropertiesConfigurationInYamlService();
				}
			}
		}
		return propertiesConfigurationInYamlService;
	}

	public void runUserInteraction(List<YamlProperties> yamlProperties) {
		if (ObjectUtils.isEmpty(yamlProperties)) {
			return;
		}
		UserPropertiesService userPropertiesService = UserPropertiesService.getInstance();
		yamlProperties.forEach(yamlProperty -> {
			validateType(yamlProperty, userPropertiesService);
			if (propertyNotExists(yamlProperty, userPropertiesService)) {
				String result = getValueFromUser(yamlProperty);
				setProperty(result, userPropertiesService, yamlProperty);
			}
		});
	}

	private void validateType(YamlProperties yamlProperties, UserPropertiesService userPropertiesService) {
		if (StringUtils.isEmpty(yamlProperties.getType()) || !yamlProperties.getType().equals(userPropertiesService.getKeyPrefix())) {
			throw new FerretException("Currently we only support one type of property (user.) add field type: user. to properties.", CommandLine.ExitCode.USAGE);
		}
	}

	private boolean propertyNotExists(YamlProperties yamlProperties, UserPropertiesService userPropertiesService) {
		Properties properties = userPropertiesService.getProperties(null);
		String key = properties.getProperty(yamlProperties.getKey());
		return key == null;
	}

	private String getValueFromUser(YamlProperties yamlProperties) {
		String result = ConsoleMessageService.sendYamlPropertyInteractionMessage(yamlProperties);
		if (!ConsoleMessageService.sendYesOrNoQuestion(result)) {
			return getValueFromUser(yamlProperties);
		}
		return result;
	}

	private void setProperty(String result, UserPropertiesService userPropertiesService, YamlProperties yamlProperties) {
		Map<String, String> mapOfProperties = new HashMap<>();
		mapOfProperties.put(yamlProperties.getKey(), result);
		userPropertiesService.setProperties(mapOfProperties);
	}
}
