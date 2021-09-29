/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.datorama.models.Input;
import com.datorama.models.YamlProperties;

public class ConsoleMessageService {
	public static void sendInputRequestMessage(Input input) {
		final StringBuilder sb = new StringBuilder();
		sb.append(OutputService.getInstance().boldPart("Input required ")).append("press enter to use default value.").append(System.lineSeparator());
		sb.append("Description: ").append(input.getRequest()).append(". ");
		sb.append("default value: ").append(input.getDefaultValue());
		OutputService.getInstance().normal(sb.toString());
	}

	public static String sendYamlPropertyInteractionMessage(YamlProperties yamlProperties) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Property ").append(yamlProperties.getType()).append(yamlProperties.getKey()).append(" need value.")
				.append(System.lineSeparator());
		sb.append("Description of what is this property: ").append(yamlProperties.getDescription()).append(System.lineSeparator());
		if (ObjectUtils.isNotEmpty(yamlProperties.getValues())) {
			availableValues(yamlProperties.getValues(), sb);
		}
		sb.append("Waiting for user input.");
		OutputService.getInstance().normal(sb.toString());
		String result = ConsoleInputService.getUserInput();
		if (StringUtils.isEmpty(result)) {
			OutputService.getInstance().error("Cannot be empty input.");
			return sendYamlPropertyInteractionMessage(yamlProperties);
		}
		if (ObjectUtils.isNotEmpty(yamlProperties.getValues())) {
			Optional<String> valueOptional = yamlProperties.getValues().stream().filter(value -> result.equals(value)).findFirst();
			if (!valueOptional.isPresent()) {
				OutputService.getInstance().error("Value must be one of the available values.");
				return sendYamlPropertyInteractionMessage(yamlProperties);
			}
		}
		return result;
	}

	private static void availableValues(List<String> values, StringBuilder sb) {
		sb.append("Those are the available values (must be one of them): ");
		sb.append("[");
		for (int i = 0; i < values.size(); i++) {
			sb.append(values.get(i));
			if (i != values.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]").append(System.lineSeparator());
	}

	/**
	 * true - yes, false - no
	 *
	 * @param valueToCheck
	 * @return
	 */
	public static boolean sendYesOrNoQuestion(String valueToCheck) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Are you sure ").append(valueToCheck).append(" is the correct value? ").append(" answer yes/no.");
		OutputService.getInstance().normal(sb.toString());
		String result = ConsoleInputService.getUserInput();
		Optional<Boolean> resultOptional = checkResultIsYesOrNo(result);
		if (!resultOptional.isPresent()) {
			return sendYesOrNoQuestion(valueToCheck);
		}
		return resultOptional.get();
	}

	public static boolean sendYesOrNoQuestionAfterWhenRequest(String valueToPrint,String customQuestion) {
		final StringBuilder sb = new StringBuilder();
		sb.append(valueToPrint).append(System.lineSeparator());
		if(StringUtils.isEmpty(customQuestion)){
			sb.append("Do you want to run it? answer y/n.");
		} else {
			sb.append(customQuestion).append(" answer y/n.");
		}
		OutputService.getInstance().normal(sb.toString());
		String result = ConsoleInputService.getUserInput();
		Optional<Boolean> resultOptional = checkResultIsYesOrNo(result);
		if (!resultOptional.isPresent()) {
			return sendYesOrNoQuestionAfterWhenRequest(valueToPrint,customQuestion);
		}
		return resultOptional.get();
	}

	/**
	 * true - yes, false - no, empty - neither
	 *
	 * @param value
	 * @return
	 */
	private static Optional<Boolean> checkResultIsYesOrNo(String value) {
		if (StringUtils.isEmpty(value)) {
			return Optional.empty();
		}
		value = value.toLowerCase();
		if (StringUtils.equalsAny(value,"y", "yes", "ye")) {
			return Optional.of(true);
		}
		if (StringUtils.equalsAny(value,"no", "n")) {
			return Optional.of(false);
		}
		return Optional.empty();
	}
}
