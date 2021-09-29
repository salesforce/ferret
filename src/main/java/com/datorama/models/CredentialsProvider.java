/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class CredentialsProvider {
	private String githubToken;
	private String githubUsername;

	public String getGithubToken() {
		return githubToken;
	}

	public void setGithubToken(String githubToken) {
		this.githubToken = githubToken;
	}

	public String getGithubUsername() {
		return githubUsername;
	}

	public void setGithubUsername(String githubUsername) {
		this.githubUsername = githubUsername;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("CredentialsProvider{");
		sb.append("githubToken='").append(githubToken).append('\'');
		sb.append(", githubUsername='").append(githubUsername).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
