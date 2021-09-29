/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

import com.datorama.common.Constants;

public class CredentialsProperties {
	private static String getFileName() {
		return "credentials.properties";
	}

	public static File getFile() {
		return Paths.get(Constants.FERRET_DIR.toString(), getFileName()).toFile();
	}

	public static Properties getProperties() {
		return FilePropertiesService.readProperties(getFile());
	}
}
