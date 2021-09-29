/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.pipelines;

import java.nio.file.Path;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class PipelineProvider {
	private Path path;
	private String keyPrefix;
	private String fileName;

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("PipelineProvider{");
		sb.append("path=").append(path);
		sb.append(", keyPrefix='").append(keyPrefix).append('\'');
		sb.append(", fileName='").append(fileName).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
