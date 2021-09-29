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
public class Condition {
	private String operator;
	private String command;
	private String compareTo;
	private String requestBoolean;
	private String customQuestion;

	public String getOperator() {
		if(StringUtils.isNotEmpty(operator)){
			RelationOperatorEnum.validate(operator);
		}
		return operator;
	}

	public String getCustomQuestion() {
		return customQuestion;
	}

	public void setCustomQuestion(String customQuestion) {
		this.customQuestion = customQuestion;
	}

	public String getRequestBoolean() {
		return requestBoolean;
	}

	public void setRequestBoolean(String requestBoolean) {
		this.requestBoolean = requestBoolean;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCompareTo() {
		return compareTo;
	}

	public void setCompareTo(String compareTo) {
		this.compareTo = compareTo;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Condition{");
		sb.append("relation='").append(operator).append('\'');
		sb.append(", run='").append(command).append('\'');
		sb.append(", compareTo='").append(compareTo).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
