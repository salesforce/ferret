/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.services.OutputService;
import com.datorama.services.properties.directory.UserPropertiesService;
import org.apache.commons.lang3.ObjectUtils;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Properties;

@ApplicationScoped
@CommandLine.Command(name = "user", description = "user properties settings set a property or get current user properties.", subcommands = { CommandLine.HelpCommand.class })
public class UserCommand implements Runnable, FerretErrorHandler {
	@CommandLine.Option(names = { "-P", "--property", "-p" }, description = "store new properties for user. example: --property key=value")
	Map<String, String> properties;
	@CommandLine.Option(names = { "-g", "--get" }, description = "show all properties currently stored in user properties.", paramLabel = "show properties")
	boolean showProperties;

	@Inject
	OutputService outputService;
	@Inject
	UserPropertiesService userPropertiesService;

	@Override public void run() {
		ferretRun(() -> {
			if (ObjectUtils.isEmpty(properties) && showProperties == false) {
				CommandLine commandLine = new CommandLine(new UserCommand());
				commandLine.usage(System.out);
			}
			if (ObjectUtils.isNotEmpty(properties)) {
				userPropertiesService.setProperties(properties);
			}
			if (showProperties) {
				Properties properties = userPropertiesService.getProperties(null);
				properties.keySet().forEach(key -> outputService.normal("key: " + key + " value: " + properties.getProperty(key.toString())));
			}
		});
	}
}
