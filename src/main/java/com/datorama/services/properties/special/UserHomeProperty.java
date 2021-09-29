/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties.special;

import java.nio.file.Path;

import com.datorama.services.interfaces.SpecialProperty;

/**
 * moved to user.properties. unsupported currently.
 */
public class UserHomeProperty implements SpecialProperty {
	@Override public String getPropertyKey() {
		return "user.home";
	}

	@Override public String getPropertyValue(Path currentPath) {
		return System.getProperty("user.home");
	}
}
