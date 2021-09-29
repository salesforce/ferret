/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.ObjectUtils;

import com.datorama.services.OutputService;
import com.datorama.services.properties.directory.UserPropertiesService;

import picocli.CommandLine;

@CommandLine.Command(name = "user", description = "user properties settings set a property or get current user properties.", subcommands = { CommandLine.HelpCommand.class })
public class UserCommand implements Runnable, FerretErrorHandler {
	@CommandLine.Option(names = { "-P", "--property", "-p" }, description = "store new properties for user. example: --property key=value")
	private Map<String, String> properties;
	@CommandLine.Option(names = { "-g", "--get" }, description = "show all properties currently stored in user properties.", paramLabel = "show properties")
	private boolean showProperties;

	@Override public void run() {
		ferretRun(() -> {
			if (ObjectUtils.isEmpty(properties) && showProperties == false) {
				CommandLine commandLine = new CommandLine(new UserCommand());
				commandLine.usage(System.out);
			}
			if (ObjectUtils.isNotEmpty(properties)) {
				UserPropertiesService userPropertiesService = UserPropertiesService.getInstance();
				userPropertiesService.setProperties(properties);
			}
			if (showProperties) {
				UserPropertiesService userPropertiesService = UserPropertiesService.getInstance();
				Properties properties = userPropertiesService.getProperties(null);
				properties.keySet().forEach(key -> OutputService.getInstance().normal("key: " + key + " value: " + properties.getProperty(key.toString())));
			}
		});
	}
}
