/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties;

import com.datorama.services.interfaces.PropertiesDirectory;
import com.datorama.services.properties.directory.UserPropertiesService;

import java.util.ArrayList;
import java.util.List;

public class DirectoryProperties {
	private static DirectoryProperties directoryProperties;
	private List<PropertiesDirectory> propertiesDirectories;

	private DirectoryProperties() {
		//Deny init
	}

	public static DirectoryProperties getInstance() {
		if (directoryProperties == null) {
			synchronized (DirectoryProperties.class) {
				if (directoryProperties == null) {
					directoryProperties = new DirectoryProperties();
					directoryProperties.init();
				}
			}
		}
		return directoryProperties;
	}

	private void init() {
		propertiesDirectories = new ArrayList<>();
		UserPropertiesService userPropertiesService = UserPropertiesService.getInstance();
		propertiesDirectories.add(userPropertiesService);
	}

	public List<PropertiesDirectory> getPropertiesDirectories() {
		return propertiesDirectories;
	}

	public void add(PropertiesDirectory propertiesDirectory) {
		propertiesDirectories.add(propertiesDirectory);
	}
}
