/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.exceptions.FerretException;
import com.datorama.mixins.PipelineOptionsMixin;
import com.datorama.services.ArgumentService;
import com.datorama.services.GitRepositoryService;
import com.datorama.services.GlobalDirectoryService;
import com.datorama.services.pipelines.PipelineProvider;
import com.datorama.services.pipelines.PipelinesRepositoryService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Files;
import java.util.Optional;

@ApplicationScoped
@CommandLine.Command(name = "arguments", aliases = { "args" }, description = "shows arguments list of specific pipeline", subcommands = { CommandLine.HelpCommand.class })
public class ArgumentsCommand implements Runnable, FerretErrorHandler {
	private final ArgumentService argumentService = ArgumentService.getInstance();
	@CommandLine.Mixin
	private PipelineOptionsMixin pipelineOptionsMixin;

	@Inject
	GlobalDirectoryService globalDirectoryService;
	@Override public void run() {
		ferretRun(() -> {
			globalDirectoryService.initialize();
			if (StringUtils.isEmpty(pipelineOptionsMixin.getPipeline()) && ObjectUtils.isEmpty(pipelineOptionsMixin.getYamlFile())) {
				CommandLine commandLine = new CommandLine(new ArgumentsCommand());
				commandLine.usage(System.out);
				return;
			}
			if (StringUtils.isNotEmpty(pipelineOptionsMixin.getPipeline())) {
				GitRepositoryService.getInstance();
				PipelinesRepositoryService pipelinesRepositoryService = PipelinesRepositoryService.getInstance();
				Optional<PipelineProvider> pipelineProviderOptional = pipelinesRepositoryService.pipelines().stream()
						.filter(pipelineProvider -> pipelineProvider.getKeyPrefix().equals(pipelineOptionsMixin.getPipeline()))
						.findFirst();
				if (pipelineProviderOptional.isPresent()) {
					argumentService.printPipelineArguments(pipelineProviderOptional.get().getPath().toFile(), pipelineOptionsMixin.getPipeline() + " does not have any arguments.");
					return;
				} else {
					throw new FerretException("Didn't find " + pipelineOptionsMixin.getPipeline() + " in the repository given. To get what pipelines you have currently type: ferret pipelines",
							CommandLine.ExitCode.USAGE);
				}
			}
			if (ObjectUtils.isNotEmpty(pipelineOptionsMixin.getYamlFile())) {
				GitRepositoryService.getInstance();
				if (!Files.exists(pipelineOptionsMixin.getYamlFile().toPath())) {
					throw new FerretException(String.format("File not found -> %s.", pipelineOptionsMixin.getYamlFile().toPath()), CommandLine.ExitCode.USAGE);
				}
				argumentService.printPipelineArguments(pipelineOptionsMixin.getYamlFile(), pipelineOptionsMixin.getYamlFile().getAbsolutePath() + " does not have any arguments.");
			}
		});
	}
}
