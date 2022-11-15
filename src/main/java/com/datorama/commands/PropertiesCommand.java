/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.services.GitRepositoryService;
import com.datorama.services.GlobalDirectoryService;
import com.datorama.services.OutputService;
import com.datorama.services.properties.DirectoryProperties;
import com.datorama.services.properties.directory.UserPropertiesService;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;

@ApplicationScoped
@CommandLine.Command(name = "properties", description = "information about properties from common repository that was configured by you.", subcommands = { CommandLine.HelpCommand.class })
public class PropertiesCommand implements Runnable, FerretErrorHandler {
	@Inject
	GlobalDirectoryService globalDirectoryService;

	@Inject
	UserPropertiesService userPropertiesService;
	@Inject
	OutputService outputService;
	@Override public void run() {
		ferretRun(() -> {
			globalDirectoryService.initialize();
			GitRepositoryService.getInstance();
			DirectoryProperties directoryProperties = DirectoryProperties.getInstance();
			outputService.normal("Properties from common repository:");
			directoryProperties.getPropertiesDirectories().forEach(propertiesDirectory -> {
				if (!propertiesDirectory.getFileName().equals(userPropertiesService.getFileName())) {
					outputService.normal("File in repository: " + propertiesDirectory.getFileName() + " prefix property in yaml: " + propertiesDirectory.getKeyPrefix());
					Properties properties = propertiesDirectory.getProperties(null);
					properties.keySet().forEach(key -> {
						outputService.normal(" property: " + key.toString() + " value: " + properties.getProperty(key.toString()));
					});
				}
			});
		});
	}
}
