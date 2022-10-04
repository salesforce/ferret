/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.models.LifeCycleEnum;
import picocli.CommandLine;

@CommandLine.Command(name = "teardown",aliases = {"tear"}, description = "runs teardown part of stages in a Ferret pipeline Yaml.", subcommands = { CommandLine.HelpCommand.class })
public class TeardownCommand extends LifeCycleCommand implements Runnable {
	@Override public void run() {
		runLifeCycle(LifeCycleEnum.TEARDOWN,new TeardownCommand());
	}
}
