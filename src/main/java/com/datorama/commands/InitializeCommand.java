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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@CommandLine.Command(name = "init", description = "Initialize default ferret configuration.", subcommands = {CommandLine.HelpCommand.class})
public class InitializeCommand implements Runnable, FerretErrorHandler {
    @Inject
    GlobalDirectoryService globalDirectoryService;
    @Inject
    OutputService outputService;
    @CommandLine.Option(names = {"-F", "--force"}, description = "cleaning up 3rd party libraries")
    boolean toDelete;

    @Override
    public void run() {
        ferretRun(() -> {
            if (toDelete) {
                globalDirectoryService.deleteSubFolder();
            }
            globalDirectoryService.initialize();
            outputService.normal("Done.");
        });
    }
}
