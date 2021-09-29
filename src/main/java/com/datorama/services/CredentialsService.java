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
import java.util.Properties;

import org.slf4j.LoggerFactory;

import com.datorama.common.Constants;
import com.datorama.models.CredentialsProvider;
import com.datorama.services.properties.CredentialsProperties;
import com.datorama.services.properties.FilePropertiesService;

import ch.qos.logback.classic.Logger;

public class CredentialsService {
	private static CredentialsService credentialsService;
	private final Logger log = (Logger) LoggerFactory.getLogger(CredentialsService.class);

	private CredentialsService() {
	}

	public static CredentialsService getInstance() {
		if (credentialsService == null) {
			synchronized (CredentialsService.class) {
				if (credentialsService == null) {
					credentialsService = new CredentialsService();
				}
			}
		}
		return credentialsService;
	}

	public void setGithubToken(String token) {
		Properties properties = getCredentialsProperties();
		properties.setProperty(Constants.GITHUB_TOKEN_PROPERTY, token);
		writeCredentialsPropertiesToFile(properties);
	}

	public void setGitHubUsername(String username) {
		Properties properties = getCredentialsProperties();
		properties.setProperty(Constants.GITHUB_USERNAME_PROPERTY, username);
		writeCredentialsPropertiesToFile(properties);
	}

	public CredentialsProvider getCredentialsProvider() {
		Properties properties = getCredentialsProperties();
		CredentialsProvider credentialsProvider = new CredentialsProvider();
		credentialsProvider.setGithubToken(properties.getProperty(Constants.GITHUB_TOKEN_PROPERTY));
		credentialsProvider.setGithubUsername(properties.getProperty(Constants.GITHUB_USERNAME_PROPERTY));
		return credentialsProvider;
	}

	public void createFile() throws IOException {
		File credentials = CredentialsProperties.getFile();
		if(!credentials.exists()){
			credentials.setExecutable(true);
			credentials.setWritable(true);
			credentials.setReadable(true);
			credentials.createNewFile();
			Properties properties = new Properties();
			properties.setProperty(Constants.GITHUB_TOKEN_PROPERTY, "");
			properties.setProperty(Constants.GITHUB_USERNAME_PROPERTY, "");
			credentials.createNewFile();
			FilePropertiesService.writeProperties(credentials, properties);

		}
	}

	private Properties getCredentialsProperties() {
		return FilePropertiesService.readProperties(CredentialsProperties.getFile());
	}
	private void writeCredentialsPropertiesToFile(Properties properties){
		FilePropertiesService.writeProperties(CredentialsProperties.getFile(),properties);
	}
}
