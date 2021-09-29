/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties.special;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.datorama.exceptions.FerretException;
import com.datorama.services.ProcessService;
import com.datorama.services.interfaces.SpecialProperty;

import picocli.CommandLine;

public class GitRootProperty implements SpecialProperty {
	private static final String GIT_ROOT_COMMAND = "git rev-parse --show-toplevel";
	private ProcessService processService = ProcessService.getInstance();

	@Override public String getPropertyKey() {
		return "git.root.directory";
	}

	@Override public String getPropertyValue(Path currentFilePath) {
		Optional<String> gitRootOptional = processService.runCommandWithResult(GIT_ROOT_COMMAND, currentFilePath.getParent());
		if (!gitRootOptional.isPresent()) {
			throw new FerretException("Failed fetching git root directory", CommandLine.ExitCode.SOFTWARE);
		}
		Path path;
		String gitRoot = gitRootOptional.get().trim();
		try {
			path = Paths.get(gitRoot);
		} catch (InvalidPathException e) {
			throw new FerretException(String.format("Path %s is invalid.", gitRoot), CommandLine.ExitCode.SOFTWARE);
		}
		return path.toString();
	}
}
