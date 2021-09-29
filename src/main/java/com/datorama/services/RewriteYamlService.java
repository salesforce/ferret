/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.datorama.exceptions.FerretException;
import com.datorama.files.FerretYamlFile;
import com.datorama.services.properties.ManagerPropertiesService;

import ch.qos.logback.classic.Logger;

public class RewriteYamlService {
	private static RewriteYamlService rewriteYamlService;
	private final Logger log = (Logger) LoggerFactory.getLogger(RewriteYamlService.class);
	ManagerPropertiesService managerPropertiesService = ManagerPropertiesService.getInstance();
	FindKeysService findKeysService = FindKeysService.getInstance();
	ReplaceKeysService replaceKeysService = ReplaceKeysService.getInstance();

	private RewriteYamlService() {
		//Deny init
	}

	public static RewriteYamlService getInstance() {
		if (rewriteYamlService == null) {
			synchronized (RewriteYamlService.class) {
				if (rewriteYamlService == null) {
					rewriteYamlService = new RewriteYamlService();
				}
			}
		}
		return rewriteYamlService;
	}

	public FerretYamlFile rewriteFerretYamlFile(File yamlFile,Map<String,String> rewriteKeys) throws FerretException {
		FileService.isFileExist(yamlFile);
		List<String> keysInFile = findKeysService.getKeys(yamlFile);
		keysInFile.removeAll(rewriteKeys.keySet());
		Map<String, String> properties = managerPropertiesService.getPropertiesToRewrite(keysInFile, yamlFile.toPath());
		properties.putAll(rewriteKeys);
		File rewriteYamlFile = replaceKeysService.rewriteLines(yamlFile, properties);
		return (FerretYamlFile) YamlFileService.loadYamlAs(rewriteYamlFile, FerretYamlFile.class);
	}
}
