/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.datorama.common.Constants;
import com.datorama.exceptions.FerretException;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

public class LineReaderService {
	private static final Logger log = (Logger) LoggerFactory.getLogger(LineReaderService.class);

	/**
	 * find all indexes of a specific charSequence in a line.
	 *
	 * @param line         to search in.
	 * @param charSequence the argument to search in the line.
	 * @return list of all indexes of the charSequence in that was found in the line.
	 */
	public static List<Integer> findIndicesOfWordInLine(String line, String charSequence) {
		List<Integer> indexes = new ArrayList<>();
		String lowerCaseTextString = line.toLowerCase();
		String lowerCaseWord = charSequence.toLowerCase();
		int wordLength = 0;
		int index = 0;
		while (index != -1) {
			index = lowerCaseTextString.indexOf(lowerCaseWord, index + wordLength);
			if (index != -1) {
				indexes.add(index);
				wordLength = charSequence.length();
			}
		}
		return indexes;
	}

	public static List<String> readLines(File file) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			log.warn("Failed reading all lines.", e);
			throw new FerretException(String.format("Failed reading from file %s.", file.getAbsolutePath()), CommandLine.ExitCode.SOFTWARE);
		}
		return lines;
	}

	public static String getValueInLine(int startIndex, int endIndex, String line) {
		return line.substring(startIndex + Constants.START_DYNAMIC_VAR.length(), endIndex).trim();
	}
}
