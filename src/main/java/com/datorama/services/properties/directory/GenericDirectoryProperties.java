/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties.directory;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

import com.datorama.services.interfaces.PropertiesDirectory;

public class GenericDirectoryProperties implements PropertiesDirectory {
	private String fileName;
	private String keyPrefix;
	private File filePath;
	private Properties properties;


	@Override public String getFileName() {
		return fileName;
	}

	@Override public String getKeyPrefix() {
		return keyPrefix;
	}

	@Override public File getFile(Path currentFilePath) {
		return filePath;
	}

	@Override public Properties getProperties(Path currentFilePath) {
		return properties;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public void setFilePath(File filePath) {
		this.filePath = filePath;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("GenericDirectoryProperties{");
		sb.append("fileName='").append(fileName).append('\'');
		sb.append(", keyPrefix='").append(keyPrefix).append('\'');
		sb.append(", filePath=").append(filePath.getAbsolutePath());
		sb.append('}');
		return sb.toString();
	}
}
