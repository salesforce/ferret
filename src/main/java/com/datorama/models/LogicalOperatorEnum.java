/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import com.datorama.exceptions.FerretException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Optional;

@RegisterForReflection
public enum LogicalOperatorEnum {
	AND("and"),OR("or");
	 final String value;

	LogicalOperatorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static void validate(String value){
		Optional<LogicalOperatorEnum> logicalOperatorEnumOptional = Arrays.stream(LogicalOperatorEnum.values()).filter(logicalOperatorEnum -> value.toLowerCase().equals(logicalOperatorEnum.getValue())).findFirst();
		if(!logicalOperatorEnumOptional.isPresent()){
			throw new FerretException(errorMessage(value), CommandLine.ExitCode.USAGE);
		}
	}

	public static LogicalOperatorEnum getLogicalOperator(String value){
		Optional<LogicalOperatorEnum> logicalOperatorEnumOptional = Arrays.stream(LogicalOperatorEnum.values()).filter(logicalOperatorEnum -> value.toLowerCase().equals(logicalOperatorEnum.getValue())).findFirst();
		if (logicalOperatorEnumOptional.isPresent()) {
			return logicalOperatorEnumOptional.get();
		}
		throw new FerretException(errorMessage(value), CommandLine.ExitCode.USAGE);
	}

	private static String errorMessage(String value) {
		return "Only supported logical operators are: [and , or]. not: " + value;
	}
}
