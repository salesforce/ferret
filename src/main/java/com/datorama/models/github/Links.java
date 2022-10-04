/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models.github;


import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Links {

	 String self;
	 String git;
	 String html;

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public String getGit() {
		return git;
	}

	public void setGit(String git) {
		this.git = git;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Links{");
		sb.append("self='").append(self).append('\'');
		sb.append(", git='").append(git).append('\'');
		sb.append(", html='").append(html).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
