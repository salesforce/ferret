/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.exceptions;

public class FerretException extends RuntimeException{
	private int exitCode;
	private String stage;


	public FerretException(String message,int exitCode) {
		super(message);
		this.exitCode = exitCode;
	}

	public FerretException(String message,int exitCode,String stage) {
		super(message);
		this.exitCode = exitCode;
	}

	public String getStage() {
		return stage;
	}

	public int getExitCode() {
		return exitCode;
	}
}
