/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {
	public static final String START_DYNAMIC_VAR = "{{";
	public static final String END_DYNAMIC_VAR = "}}";
	public static final String FERRET_DIR_NAME = ".ferret";
	public static final String USER_HOME_PATH = System.getProperty("user.home");
	public static final Path FERRET_DIR = Paths.get(USER_HOME_PATH + "/" + FERRET_DIR_NAME +"/");
	public static final String GITHUB_TOKEN_PROPERTY = "github.token";
	public static final String GITHUB_USERNAME_PROPERTY = "github.username";
	public static final Path REMOTE_DIR = Paths.get(FERRET_DIR.toString(), "remote");
	public static final Path GIT_REPO_DIR = Paths.get(FERRET_DIR.toString(),"git-repository");
	public static final Path FERRET_TEMP_DIR = Paths.get(FERRET_DIR.toString(), "temp");
	public static final Path FERRET_TEMP_SCRIPTS_DIR = Paths.get(FERRET_TEMP_DIR.toString(),"scripts");

}
