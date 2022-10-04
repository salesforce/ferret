/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.services.CredentialsService;
import com.datorama.services.GlobalDirectoryService;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "credentials",aliases = {"creds"}, description = "Credentials settings for different version control (github) that need authentication.", subcommands = { CommandLine.HelpCommand.class })
public class CredentialsCommand implements Runnable, FerretErrorHandler{
	private static final Logger log = Logger.getLogger(CredentialsCommand.class);
	@CommandLine.Option(names = { "--token", "-t" }, description = "Token for github", defaultValue = "")
	private String token;
	@CommandLine.Option(names = { "--username", "-u" }, description = "username for github", defaultValue = "")
	private String username;
	private final GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();

	private CredentialsService credentialsService = CredentialsService.getInstance();
	@Override public void run() {
		ferretRun(() -> {
			globalDirectoryService.initialize();
			if (StringUtils.isAllEmpty(token, username)) {
				CommandLine commandLine = new CommandLine(new CredentialsCommand());
				commandLine.usage(System.out);
				return;
			}
			if(StringUtils.isNotEmpty(token)){
				credentialsService.setGithubToken(token);
			}
			if(StringUtils.isNotEmpty(username)){
				credentialsService.setGitHubUsername(username);
			}
		});

	}
}
