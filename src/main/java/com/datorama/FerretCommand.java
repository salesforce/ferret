/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama;

import org.slf4j.LoggerFactory;

import com.datorama.commands.*;

import ch.qos.logback.classic.Logger;
import io.micronaut.configuration.picocli.MicronautFactory;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * TODO- explore subject of init before execution @link https://picocli.info/#_initialization_before_execution
 */
@Command(name = "ferret", description = "Ferret - Pipeline for your local environment setup",
		mixinStandardHelpOptions = true, version = { "Ferret version 1.0.12",
		"Picocli " + picocli.CommandLine.VERSION,
		"JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
		"OS: ${os.name} ${os.version} ${os.arch}" },
		subcommands = { CommandLine.HelpCommand.class, SetupCommand.class,
				InitializeCommand.class, CredentialsCommand.class, RepositoryCommand.class,
		PipelineCommand.class,PropertiesCommand.class,UserCommand.class,
		TeardownCommand.class,ArgumentsCommand.class})
public class FerretCommand extends BaseCommand implements Runnable {
	private final Logger log = (Logger) LoggerFactory.getLogger(FerretCommand.class);

	public static void main(String[] args) {
		FerretCommand ferretCommand = new FerretCommand();
		int exitCode = ferretCommand.execute(FerretCommand.class, args);
		System.exit(exitCode);
	}

	private int execute(Class<?> clazz, String[] args) {
		try (ApplicationContext context = ApplicationContext.build(
				clazz, Environment.CLI).start()) {
			return new CommandLine(clazz, new MicronautFactory(context)).
					execute(args);
		}
	}

	@Override public void run() {
		CommandLine commandLine = new CommandLine(new FerretCommand());
		commandLine.usage(System.out);
	}
}
