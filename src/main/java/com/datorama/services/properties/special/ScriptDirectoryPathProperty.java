/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties.special;

import java.nio.file.Path;

import javax.inject.Singleton;

import com.datorama.services.interfaces.SpecialProperty;

@Singleton
public class ScriptDirectoryPathProperty implements SpecialProperty {
	@Override public String getPropertyKey() {
		return "script.directory.path";
	}

	@Override public String getPropertyValue(Path currentPath) {
		return currentPath.toAbsolutePath().getParent().toString();
	}
}
