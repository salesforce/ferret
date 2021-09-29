/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.services.GlobalDirectoryService;
import com.datorama.services.OutputService;

import picocli.CommandLine;

@CommandLine.Command(name = "init", description = "Initialize default ferret configuration.", subcommands = { CommandLine.HelpCommand.class })
public class InitializeCommand implements Runnable, FerretErrorHandler {
	@CommandLine.Option(names = { "-F", "--force" }, description = "cleaning up 3rd party libraries")
	private boolean toDelete;
	 GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();
	@Override public void run() {
		ferretRun(()-> {
			if(toDelete){
				globalDirectoryService.deleteSubFolder();
			}
			globalDirectoryService.initialize();
			OutputService.getInstance().normal("Done.");
		});
	}
}
