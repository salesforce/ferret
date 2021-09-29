/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import java.util.Arrays;
import java.util.Optional;

import com.datorama.exceptions.FerretException;

import io.micronaut.core.annotation.Introspected;
import picocli.CommandLine;

@Introspected
public enum RelationOperatorEnum {
	EQUALS("equals"),
	NOT_EQUALS("not_equals"),
	CONTAINS("contains"),
	NOT_CONTAINS("not_contains"),
	EXIT_CODE_EQUALS("exit_code_equals"),
	EXIT_CODE_NOT_EQUALS("exit_code_not_equals");


	private String value;
	RelationOperatorEnum(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Optional<RelationOperatorEnum> getRelationOperator(String value) {
		Optional<RelationOperatorEnum> conditionOptional = Arrays.stream(RelationOperatorEnum.values()).filter(relation -> value.toLowerCase().equals(relation.getValue())).findFirst();
		if (conditionOptional.isPresent()) {
			return conditionOptional;
		} else {
			throw new FerretException(missingRelationMessage(value), CommandLine.ExitCode.USAGE);
		}
	}

	public static void validate(String value) {
		Optional<RelationOperatorEnum> conditionOptional = Arrays.stream(RelationOperatorEnum.values()).filter(relation -> value.toLowerCase().equals(relation.getValue())).findFirst();
		if (!conditionOptional.isPresent()) {
			throw new FerretException(missingRelationMessage(value), CommandLine.ExitCode.USAGE);
		}
	}

	private static String missingRelationMessage(String value) {
		final StringBuilder sb = new StringBuilder();
		sb.append(value).append(" is not a valid relation, ferret support: ");
		Arrays.stream(RelationOperatorEnum.values()).forEach(condition -> sb.append(condition.getValue()).append(" "));
		sb.append(".").append(System.lineSeparator());
		return sb.toString();
	}
}
