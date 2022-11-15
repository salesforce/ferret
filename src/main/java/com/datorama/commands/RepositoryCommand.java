/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.exceptions.FerretException;
import com.datorama.models.RepositoryProvider;
import com.datorama.services.OutputService;
import com.datorama.services.properties.RepositoryPropertiesService;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
@CommandLine.Command(name = "repository", aliases = { "repo" }, description = "Settings for repository to get all ferret common pipeline and properties.", subcommands = {
		CommandLine.HelpCommand.class })
public class RepositoryCommand implements Runnable, FerretErrorHandler {
	@CommandLine.Option(names = { "--get", "-g" }, description = "show current repository settings.", paramLabel = "show settings")
	private boolean showSettings;
	@CommandLine.Option(names = { "-o", "--owner" }, description = "owner name of the repository")
	private String owner;
	@CommandLine.Option(names = { "-r", "--repository" }, description = "the repository name")
	private String repository;
	@CommandLine.Option(names = { "-b", "--branch" }, description = "the repository branch name")
	private String branch;

	@Inject
	OutputService outputService;
	@Inject
	RepositoryPropertiesService repositoryPropertiesService;

	@Override public void run() {
		ferretRun(() -> {
			if (!showSettings && StringUtils.isAllEmpty(owner, repository, branch)) {
				CommandLine commandLine = new CommandLine(new RepositoryCommand());
				commandLine.usage(System.out);
				return;
			}
			boolean setSettings = false;
			if (StringUtils.isNoneEmpty(owner, repository, branch)) {
				RepositoryProvider repositoryProvider = new RepositoryProvider();
				repositoryProvider.setRepository(repository);
				repositoryProvider.setOwner(owner);
				repositoryProvider.setBranch(branch);
				repositoryPropertiesService.addProperties(repositoryProvider);
				outputService.normal("Repository settings set.");
				setSettings = true;
			}
			if (showSettings) {
				Optional<RepositoryProvider> repositoryProviderOptional = repositoryPropertiesService.getProperties();
				if (!repositoryProviderOptional.isPresent()) {
					outputService.normal("No settings for repository.");
				} else {
					RepositoryProvider repositoryProvider = repositoryProviderOptional.get();
					outputService.normal("Repository settings are: ");
					outputService.normal(RepositoryPropertiesService.OWNER_NAME + ": " + repositoryProvider.getOwner());
					outputService.normal(RepositoryPropertiesService.REPOSITORY_NAME + ": " + repositoryProvider.getRepository());
					outputService.normal(RepositoryPropertiesService.BRANCH_NAME + ": " + repositoryProvider.getBranch());
				}
			}
			//when support open source only to return this
			if (!setSettings) {
				throw new FerretException("when you set settings for repository, ferret needs owner,repository and branch. use 'ferret repository' for help", CommandLine.ExitCode.USAGE);
			}
		});
	}
}
