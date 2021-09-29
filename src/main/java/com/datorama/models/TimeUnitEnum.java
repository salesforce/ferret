/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import com.datorama.exceptions.FerretException;

import picocli.CommandLine;

public enum TimeUnitEnum {
	SECONDS("s"),MINUTES("m");
	private final String value;

	TimeUnitEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Duration getDuration(String value) throws FerretException {
		Optional<TimeUnitEnum> timeUnitEnumOptional = Arrays.stream(TimeUnitEnum.values())
															.filter(timeUnitEnum -> value.lastIndexOf(timeUnitEnum.getValue()) > 0)
															.findFirst();
		if (!timeUnitEnumOptional.isPresent()) {
			throw new FerretException(errorMessage(value), CommandLine.ExitCode.USAGE);
		}
		int number;
		try {
			 number = Integer.parseInt(value.substring(0,value.length()-1));
		} catch (NumberFormatException e){
			throw new FerretException(errorMessage(value), CommandLine.ExitCode.USAGE);
		}
		switch (timeUnitEnumOptional.get()) {
			case SECONDS:
				return Duration.ofSeconds(number);
			case MINUTES:
				return Duration.ofMinutes(number);
		}
		throw new FerretException("Was not suppose to reach here, it's a bug.", CommandLine.ExitCode.SOFTWARE);
	}

	private static String errorMessage(String value) {
		return value + " is incorrect. Time unit is written as number with timeunit (seconds or minutes) example: 60s (60 seconds) or 30m (30 minutes)";
	}

}
