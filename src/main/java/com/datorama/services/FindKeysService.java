/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.common.Constants;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Find keys in a file.
 */
@ApplicationScoped
public class FindKeysService {
	static final Logger log = Logger.getLogger(FindKeysService.class);

	public List<String> getKeys(File file) {
		log.debug("Get Keys from file: " + file.getAbsolutePath());
		List<String> lines = LineReaderService.readLines(file);
		List<String> keys = new ArrayList<>();
		for (int i = 0; i <= lines.size() - 1; i++) {
			keys.addAll(getKeysInLine(lines.get(i)));
		}
		return keys;
	}

	public List<String> getKeysInLine(String line) {
		List<Integer> startIndices = LineReaderService.findIndicesOfWordInLine(line, Constants.START_DYNAMIC_VAR);
		if (startIndices.size() == 0) {
			return Collections.emptyList();
		}
		List<Integer> endIndices = LineReaderService.findIndicesOfWordInLine(line, Constants.END_DYNAMIC_VAR);
		if (startIndices.size() != endIndices.size()) {
			return Collections.emptyList();
		}
		List<String> keys = new ArrayList<>();
		for (int i = 0; i <= startIndices.size() - 1; i++) {
			int startIndex = startIndices.get(i);
			int endIndex = endIndices.get(i);
			String key = LineReaderService.getValueInLine(startIndex, endIndex, line);
			keys.add(key);
		}
		return keys;
	}
}
