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
public class Input {
	private String key;
	private String request;
	private String defaultValue;
	private String command;
	private RemoteConfig remote;

	public RemoteConfig getRemote() {
		return remote;
	}

	public void setRemote(RemoteConfig remote) {
		this.remote = remote;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Input{");
		sb.append("key='").append(key).append('\'');
		sb.append(", request='").append(request).append('\'');
		sb.append(", defaultValue='").append(defaultValue).append('\'');
		sb.append(", command='").append(command).append('\'');
		sb.append(", remote=").append(remote);
		sb.append('}');
		return sb.toString();
	}
}
