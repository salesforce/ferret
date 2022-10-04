/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties.special;

import com.datorama.services.interfaces.SpecialProperty;

import javax.inject.Singleton;
import java.nio.file.Path;

@Singleton
public class ScriptDirectoryPathProperty implements SpecialProperty {
	@Override public String getPropertyKey() {
		return "script.directory.path";
	}

	@Override public String getPropertyValue(Path currentPath) {
		return currentPath.toAbsolutePath().getParent().toString();
	}
}
