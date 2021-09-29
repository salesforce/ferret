/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Apply {
	private String file;
	private String stage;
	private RemoteConfig remote;
	private String pipeline;
	private List<Argument> arguments;

	public List<Argument> getArguments() {
		if (ObjectUtils.isEmpty(arguments)) {
			return Collections.emptyList();
		}
		return arguments;
	}

	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	public String getPipeline() {
		return pipeline;
	}

	public void setPipeline(String pipeline) {
		this.pipeline = pipeline;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public RemoteConfig getRemote() {
		return remote;
	}

	public void setRemote(RemoteConfig remote) {
		this.remote = remote;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Apply{");
		sb.append("file='").append(file).append('\'');
		sb.append(", stage='").append(stage).append('\'');
		sb.append(", remote=").append(remote);
		sb.append(", pipeline='").append(pipeline).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
