/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.mixins;

import picocli.CommandLine;

import java.io.File;

public class PipelineOptionsMixin {
	@CommandLine.Option(names = { "--file", "-f" }, description = "Yaml file.", paramLabel = "yaml file")
	private File yamlFile;
	@CommandLine.Option(names = { "--pipeline", "-p" }, description = "pipeline from repository to run.")
	private String pipeline;

	public String getPipeline() {
		return pipeline;
	}

	public File getYamlFile() {
		return yamlFile;
	}
}
