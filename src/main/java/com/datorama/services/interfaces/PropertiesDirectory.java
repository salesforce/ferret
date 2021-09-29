/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.interfaces;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;


public interface PropertiesDirectory {
	String getFileName();

	String getKeyPrefix();

	File getFile(Path currentFilePath);

	Properties getProperties(Path currentFilePath);
}
