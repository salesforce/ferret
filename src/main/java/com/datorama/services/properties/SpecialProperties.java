/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties;

import com.datorama.services.interfaces.SpecialProperty;
import com.datorama.services.properties.special.GitRootProperty;
import com.datorama.services.properties.special.ScriptDirectoryPathProperty;

import java.util.ArrayList;
import java.util.List;

public class SpecialProperties {
	private static SpecialProperties specialProperties;
	private List<SpecialProperty> specialPropertiesList;

	private SpecialProperties() {
		//Deny init
	}


	public static SpecialProperties getInstance() {
		if (specialProperties == null) {
			synchronized (SpecialProperties.class) {
				if (specialProperties == null) {
					specialProperties = new SpecialProperties();
					specialProperties.init();
				}
			}
		}
		return specialProperties;
	}

	private void init() {
		GitRootProperty gitRootProperty = new GitRootProperty();
		ScriptDirectoryPathProperty scriptDirectoryPathProperty = new ScriptDirectoryPathProperty();
		specialPropertiesList = new ArrayList<>();
		specialPropertiesList.add(gitRootProperty);
		specialPropertiesList.add(scriptDirectoryPathProperty);
	}

	public List<SpecialProperty> getSpecialProperties() {
		return specialPropertiesList;
	}
}
