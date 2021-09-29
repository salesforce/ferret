/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import org.apache.commons.lang3.StringUtils;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Argument {

	private String key;
	private String value;
	private String defaultValue;
	private String description;

	public String getDescription() {
		if (StringUtils.isEmpty(description)) {
			return "";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultValue() {
		if (StringUtils.isEmpty(defaultValue)) {
			return "";
		}
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Argument{");
		sb.append("key='").append(key).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append(", defaultValue='").append(defaultValue).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
