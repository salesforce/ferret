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
import com.datorama.services.pipelines.PipelinesRepositoryService;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@CommandLine.Command(name = "pipelines", description = "Information about all pipelines from the common repository that was configured by you.", subcommands = {
		CommandLine.HelpCommand.class })
public class PipelineCommand implements Runnable, FerretErrorHandler {
	private final GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();
	
	@Inject
	OutputService outputService;

	@Override public void run() {
		ferretRun(() -> {
			globalDirectoryService.initialize();
			GitRepositoryService.getInstance();
			PipelinesRepositoryService pipelinesRepositoryService = PipelinesRepositoryService.getInstance();
			outputService.normal("To run one of those pipelines, type: ferret setup --pipeline <pipeline-name>");
			outputService.normal("Pipelines from common repository (in pipelines directory): ");
			pipelinesRepositoryService.pipelines()
					.forEach(pipelineProvider -> outputService.normal("pipeline: " + pipelineProvider.getKeyPrefix() + " | file in repository: " + pipelineProvider.getFileName()));
		});
	}
}
