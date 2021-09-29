/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.common.Artwork;
import com.datorama.services.GitRepositoryService;
import com.datorama.services.GlobalDirectoryService;
import com.datorama.services.OutputService;
import com.datorama.services.pipelines.PipelinesRepositoryService;

import picocli.CommandLine;

@CommandLine.Command(name = "pipelines", description = "Information about all pipelines from the common repository that was configured by you.", subcommands = {
		CommandLine.HelpCommand.class })
public class PipelineCommand implements Runnable, FerretErrorHandler {
	private final GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();

	@Override public void run() {
		ferretRun(() -> {
			OutputService.getInstance().normal(Artwork.PIPELINE_ARTWORK.getValue());
			globalDirectoryService.initialize();
			GitRepositoryService.getInstance();
			PipelinesRepositoryService pipelinesRepositoryService = PipelinesRepositoryService.getInstance();
			OutputService.getInstance().normal("To run one of those pipelines, type: ferret setup --pipeline <pipeline-name>");
			OutputService.getInstance().normal("Pipelines from common repository (in pipelines directory): ");
			pipelinesRepositoryService.pipelines()
					.forEach(pipelineProvider -> OutputService.getInstance().normal("pipeline: " + pipelineProvider.getKeyPrefix() + " | file in repository: " + pipelineProvider.getFileName()));
		});
	}
}
