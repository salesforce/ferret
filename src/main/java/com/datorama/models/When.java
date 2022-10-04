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

import java.util.Collections;
import java.util.List;

@RegisterForReflection
public class When {
	 List<Condition> conditions;
	 Condition condition;
	 String operator;
	 String timeout;
	 String retryInterval;
	 boolean fail;

	public boolean isFail() {
		return fail;
	}

	public void setFail(boolean fail) {
		this.fail = fail;
	}

	public List<Condition> getConditions() {
		if(conditions == null){
			return Collections.emptyList();
		}
		return conditions;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(String retryInterval) {
		this.retryInterval = retryInterval;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public String getOperator() {
		if(StringUtils.isNotEmpty(operator)){
			LogicalOperatorEnum.validate(operator);
		}
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("When{");
		sb.append("conditions=").append(getConditions());
		sb.append(", condition=").append(condition);
		sb.append(", operator='").append(operator).append('\'');
		sb.append(", timeout=").append(timeout);
		sb.append(", retryInterval=").append(retryInterval);
		sb.append(", fail=").append(fail);
		sb.append('}');
		return sb.toString();
	}
}
