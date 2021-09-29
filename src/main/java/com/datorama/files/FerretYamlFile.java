/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.files;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.datorama.models.*;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class FerretYamlFile {
	private Introduction introduction;
	private List<YamlProperties> properties;
	private Map<String, StageAttributes> stages;
	private String directory;
	private List<Input> inputs;
	private List<Argument> arguments;
	private When when;

	public When getWhen() {
		return when;
	}

	public void setWhen(When when) {
		this.when = when;
	}

	public List<Argument> getArguments() {
		if (ObjectUtils.isEmpty(arguments)) {
			return Collections.emptyList();
		}
		return arguments;
	}

	public Introduction getIntroduction() {
		return introduction;
	}

	public void setIntroduction(Introduction introduction) {
		this.introduction = introduction;
	}

	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	public List<YamlProperties> getProperties() {
		if (ObjectUtils.isEmpty(properties)) {
			return Collections.emptyList();
		}
		return properties;
	}

	public void setProperties(List<YamlProperties> properties) {
		this.properties = properties;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public List<Input> getInputs() {
		if (ObjectUtils.isEmpty(inputs)) {
			return Collections.emptyList();
		}
		return inputs;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}

	public Map<String, StageAttributes> getStages() {
		if (ObjectUtils.isEmpty(stages)) {
			return Collections.emptyMap();
		}
		return stages;
	}

	public void setStages(Map<String, StageAttributes> stages) {
		this.stages = stages;
	}

	private String stagesMapToString() {
		final StringBuilder sb = new StringBuilder("Stages{");
		if (stages != null) {
			stages.forEach((string, stageAttributes) -> sb.append("[")
					.append(string)
					.append(",")
					.append(stageAttributes.toString())
					.append("]"));
		}
		sb.append('}');
		return sb.toString();
	}

	private String inputListToString() {
		final StringBuilder sb = new StringBuilder("Inputs{");
		if (inputs != null) {
			inputs.forEach((input) -> sb.append("[")
					.append(input.toString())
					.append("]"));
		}
		sb.append('}');
		return sb.toString();
	}

	private String argumentListToString() {
		final StringBuilder sb = new StringBuilder("Arguments{");
		if (arguments != null) {
			arguments.forEach((argument) -> sb.append("[")
					.append(argument.toString())
					.append("]"));
		}
		sb.append('}');
		return sb.toString();
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("FerretYamlFile{");
		sb.append("stages=").append(stagesMapToString());
		sb.append(", directory='").append(directory).append('\'');
		sb.append(", inputs=").append(inputListToString());
		sb.append(", arguments=").append(argumentListToString());
		sb.append(", properties=").append(properties);
		sb.append('}');
		return sb.toString();
	}
}
