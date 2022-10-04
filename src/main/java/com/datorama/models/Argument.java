/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.commons.lang3.StringUtils;

@RegisterForReflection
public class Argument {

    String key;
    String value;
    String defaultValue;
    String description;

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

    @Override
    public String toString() {
		String sb = "Argument{" + "key='" + key + '\'' +
				", value='" + value + '\'' +
				", defaultValue='" + defaultValue + '\'' +
				", description='" + description + '\'' +
				'}';
        return sb;
    }
}
