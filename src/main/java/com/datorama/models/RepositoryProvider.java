/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;


import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class RepositoryProvider {
	 String owner;
	 String repository;
	 String branch;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("RepositoryProvider{");
		sb.append("owner='").append(owner).append('\'');
		sb.append(", repository='").append(repository).append('\'');
		sb.append(", branch='").append(branch).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
