/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import java.util.Optional;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class ProcessResponse {
	private int exitCode;
	private Optional<String> output;

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public Optional<String> getOutput() {
		return output;
	}

	public void setOutput(Optional<String> output) {
		this.output = output;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("ProcessResponse{");
		sb.append("exitCode=").append(exitCode);
		sb.append(", output=").append(output);
		sb.append('}');
		return sb.toString();
	}
}
