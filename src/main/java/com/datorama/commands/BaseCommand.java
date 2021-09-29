/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import org.slf4j.LoggerFactory;

import com.datorama.services.OutputService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

public class BaseCommand {
	@CommandLine.Option(names = { "-X", "--debug" }, scope = CommandLine.ScopeType.INHERIT, hidden = true)
	public void setDebug(boolean isDebugEnabled) {
		if (isDebugEnabled) {
			Logger root = (Logger) LoggerFactory.getLogger("com.datorama");
			root.setLevel(Level.DEBUG);
			OutputService.getInstance().normal("Print logs with debug level or higher.");
		}
	}
}
