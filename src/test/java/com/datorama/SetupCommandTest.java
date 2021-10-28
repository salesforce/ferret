/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.datorama.services.ProcessService;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class SetupCommandTest {
	@Test
	public void applyTest() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {


		}
	}


}
