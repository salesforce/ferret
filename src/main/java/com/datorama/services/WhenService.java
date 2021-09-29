/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.datorama.exceptions.FerretException;
import com.datorama.models.*;
import com.datorama.services.when.ExecuteCondition;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

public class WhenService {
	public static final String DEFAULT_TIMEOUT = "0s";
	public static final String DEFAULT_RETRY_INTERVAL = "5s";
	private static WhenService whenService;
	private final Logger log = (Logger) LoggerFactory.getLogger(WhenService.class);
	private final ProcessService processService = ProcessService.getInstance();

	private WhenService() {
		//Deny init
	}

	public static WhenService getInstance() {
		if (whenService == null) {
			synchronized (WhenService.class) {
				if (whenService == null) {
					whenService = new WhenService();
				}
			}
		}
		return whenService;
	}

	public boolean evaluate(When when, Path stageDirectory) throws FerretException {
		if (ObjectUtils.isEmpty(when)) {
			log.debug("no when");
			return true;
		}
		if (ObjectUtils.isEmpty(when.getCondition()) && ObjectUtils.isEmpty(when.getConditions())) {
			log.debug("empty when");
			return true;
		}
		if (ObjectUtils.isNotEmpty(when.getConditions()) && ObjectUtils.isNotEmpty(when.getCondition())) {
			throw new FerretException("Required only condition or conditions, not both.", CommandLine.ExitCode.USAGE);
		}
		boolean result;
		if (ObjectUtils.isNotEmpty(when.getCondition())) {
			log.debug("run when one condition");
			result = handleTimeout(when, () -> executeCondition(when.getCondition(), stageDirectory));
			return handleFail(when, result);
		}
		if (ObjectUtils.isNotEmpty(when.getConditions())) {
			result = handleTimeout(when, () -> {
				LogicalOperatorEnum logicalOperatorEnum = LogicalOperatorEnum.getLogicalOperator(when.getOperator());
				List<Condition> conditionList = when.getConditions();
				boolean supplyResult;
				switch (logicalOperatorEnum) {
					case OR:
						Optional<Condition> conditionOROptional = Optional.empty();
						for (Condition condition1 : conditionList) {
							if (executeCondition(condition1, stageDirectory)) {
								conditionOROptional = Optional.of(condition1);
								break;
							}
						}
						supplyResult = conditionOROptional.isPresent();
						break;
					case AND:
						Optional<Condition> conditionANDOptional = Optional.empty();
						for (Condition condition : conditionList) {
							if (!executeCondition(condition, stageDirectory)) {
								conditionANDOptional = Optional.of(condition);
								break;
							}
						}
						supplyResult = !conditionANDOptional.isPresent();
						break;
					default:
						throw new IllegalStateException("Unexpected value: " + logicalOperatorEnum);
				}
				return supplyResult;
			});
			return handleFail(when, result);
		}
		log.warn("Was not suppose to reach here.");
		return true;
	}

	private boolean executeCondition(Condition condition, Path stageDirectory) throws FerretException {
		validateCondition(condition);
		if (StringUtils.isNotEmpty(condition.getRequestBoolean())) {
			return ConsoleMessageService.sendYesOrNoQuestionAfterWhenRequest(condition.getRequestBoolean(),condition.getCustomQuestion());
		}
		Optional<RelationOperatorEnum> relationOperator = RelationOperatorEnum.getRelationOperator(condition.getOperator());
		int compareTo;
		boolean toRun = false;
		log.debug("relation for condition is " + relationOperator.get() + " command is " + condition.getCommand() + " comparing to " + condition.getCompareTo());
		switch (relationOperator.get()) {
			case EQUALS:
				Optional<ProcessResponse> equalsOptional = processService.runCommand(condition.getCommand(), stageDirectory);
				if (equalsOptional.isPresent() && equalsOptional.get().getOutput().get().equals(condition.getCompareTo())) {
					toRun = true;
				}
				break;
			case CONTAINS:
				Optional<ProcessResponse> containsOptional = processService.runCommand(condition.getCommand(), stageDirectory);
				if (containsOptional.isPresent() && containsOptional.get().getOutput().get().contains(condition.getCompareTo())) {
					toRun = true;
				}
				break;
			case NOT_CONTAINS:
				Optional<ProcessResponse> notContainsOptional = processService.runCommand(condition.getCommand(), stageDirectory);
				if (notContainsOptional.isPresent() && !notContainsOptional.get().getOutput().get().contains(condition.getCompareTo())) {
					toRun = true;
				}
				break;
			case NOT_EQUALS:
				Optional<ProcessResponse> notEqualsOptional = processService.runCommand(condition.getCommand(), stageDirectory);
				if ( notEqualsOptional.isPresent() && !notEqualsOptional.get().getOutput().get().equals(condition.getCompareTo())) {
					toRun = true;
				}
				break;
			case EXIT_CODE_EQUALS:
				Optional<Integer> equalsExitCode = processService.runCommandExitCode(condition.getCommand(), stageDirectory);
				compareTo = parseInt(condition.getCompareTo());
				if (equalsExitCode.get() == compareTo) {
					toRun = true;
				}
				break;
			case EXIT_CODE_NOT_EQUALS:
				Optional<Integer> notEqualsExitCode = processService.runCommandExitCode(condition.getCommand(), stageDirectory);
				compareTo = parseInt(condition.getCompareTo());
				if (notEqualsExitCode.get() != compareTo) {
					toRun = true;
				}
				break;
			default:
				log.warn("Not suppose to reach here.");
				break;
		}
		return toRun;
	}

	private Integer parseInt(String value) throws FerretException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new FerretException("Required Integer, received " + value, CommandLine.ExitCode.USAGE);
		}
	}

	private void validateCondition(Condition condition) throws FerretException {
		if ((StringUtils.isEmpty(condition.getOperator()) || StringUtils.isEmpty(condition.getCompareTo()) || StringUtils.isEmpty(condition.getCommand())) && StringUtils.isEmpty(condition.getRequestBoolean())) {
			throw new FerretException("Required to fill relation, compareTo and command or requestBoolean.", CommandLine.ExitCode.USAGE);
		}
	}

	private boolean handleTimeout(When when, ExecuteCondition executeCondition) throws FerretException {
		Duration timeout;
		if (StringUtils.isNotEmpty(when.getTimeout())) {
			timeout = TimeUnitEnum.getDuration(when.getTimeout());
		} else {
			timeout = TimeUnitEnum.getDuration(DEFAULT_TIMEOUT);
		}
		Duration retryInterval;
		if (StringUtils.isNotEmpty(when.getRetryInterval())) {
			retryInterval = TimeUnitEnum.getDuration(when.getRetryInterval());
		} else {
			retryInterval = TimeUnitEnum.getDuration(DEFAULT_RETRY_INTERVAL);
		}
		log.debug(when.toString() + ", timeout set to " + timeout.toString() + " retryInterval set to " + retryInterval.toString());
		Clock clock = Clock.systemDefaultZone();
		Instant end = clock.instant().plus(timeout);
		while (true) {
			boolean result = executeCondition.execute();
			if (result) {
				return true;
			}
			if (end.isBefore(clock.instant())) {
				return false;
			}
			try {
				Thread.sleep(retryInterval.toMillis());
			} catch (InterruptedException e) {
				log.warn("Failed in sleeping thread", e);
			}
		}
	}

	private boolean handleFail(When when, boolean result) throws FerretException {
		if (when.isFail()) {
			if (!result) {
				throw new FerretException("Failed in when condition of the stage.", CommandLine.ExitCode.USAGE);
			}
		}
		return result;
	}

}
