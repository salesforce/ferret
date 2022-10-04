/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

@RegisterForReflection
public class YamlProperties {
	String key;
	String type;
	String description;
	List<String> values;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		if (StringUtils.isEmpty(type)) {
			//adding default value if empty
			return "user.";
		}
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getValues() {
		if (ObjectUtils.isEmpty(values)) {
			return Collections.emptyList();
		}
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("YamlProperties{");
		sb.append("key='").append(key).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append(", values='").append(getValues()).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
