/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Command {
	private String directory;
	private String command;
	private Apply apply;

	public Apply getApply() {
		return apply;
	}

	public void setApply(Apply apply) {
		this.apply = apply;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Command{");
		sb.append("directory='").append(directory).append('\'');
		sb.append(", command='").append(command).append('\'');
		sb.append(", apply=").append(apply);
		sb.append('}');
		return sb.toString();
	}
}
