/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import org.jboss.logging.Logger;

import java.io.Console;

public class ConsoleInputService {
	private static final Logger log = Logger.getLogger(ConsoleInputService.class);

	public static String getUserInput() {
		Console console = System.console();
		String result = console.readLine();
		log.debug("Result from user input: " + result);
		return result;
	}
}
