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
public class StageAttributes {
	private String description;
	private String directory;
	private List<Command> setup;
	private List<Command> teardown;
	private List<Input> inputs;
	private When when;
	private OnFailure onFailure;
	private Summary summary;

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public String getDescription() {
		if (description != null) {
			return description;
		}
		return "";
	}

	public List<Command> getTeardown() {
		if (ObjectUtils.isEmpty(teardown)) {
			return Collections.emptyList();
		}
		return teardown;
	}

	public OnFailure getOnFailure() {
		return onFailure;
	}

	public void setOnFailure(OnFailure onFailure) {
		this.onFailure = onFailure;
	}

	public void setTeardown(List<Command> teardown) {
		this.teardown = teardown;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public List<Command> getSetup() {
		if(ObjectUtils.isEmpty(setup)){
			return Collections.emptyList();
		}
		return setup;
	}

	public void setSetup(List<Command> setup) {
		this.setup = setup;
	}

	public List<Input> getInputs() {
		return inputs;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}

	public When getWhen() {
		return when;
	}

	public void setWhen(When when) {
		this.when = when;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("StageAttributes{");
		sb.append("description='").append(description).append('\'');
		sb.append(", directory='").append(directory).append('\'');
		sb.append(", setup=").append(setupCommandsListToString());
		sb.append(", teardown=").append(teardownCommandsListToString());
		sb.append(", input=").append(inputs);
		sb.append(", when=").append(when);
		sb.append('}');
		return sb.toString();
	}

	private String setupCommandsListToString() {
		final StringBuilder sb = new StringBuilder("{");
		if (setup != null) {
			setup.forEach((command) -> sb.append("[")
					.append(command.toString())
					.append("]"));
		}
		sb.append('}');
		return sb.toString();
	}
	private String teardownCommandsListToString() {
		final StringBuilder sb = new StringBuilder("{");
		if (teardown != null) {
			teardown.forEach((command) -> sb.append("[")
					.append(command.toString())
					.append("]"));
		}
		sb.append('}');
		return sb.toString();
	}
}
