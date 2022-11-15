/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class ExitService {

	private static final Logger log = Logger.getLogger(ExitService.class);
	private static ExitService exitService;

	@Inject
	OutputService outputService;

	private ExitService() {
		//Deny init
	}

	public static ExitService getInstance() {
		if (exitService == null) {
			synchronized (ExitService.class) {
				if (exitService == null) {
					exitService = new ExitService();
				}
			}
		}
		return exitService;
	}

	public void exit(String value, int exitCode) {
		outputService.error(value);
		System.exit(exitCode);
	}
}
