/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import io.micronaut.core.annotation.Introspected;

/**
 * summary object in yaml
 */
@Introspected
public class Summary {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Summary{");
		sb.append("message='").append(message).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
