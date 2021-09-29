/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models;

import java.time.Duration;
import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class SummaryStage {
	private String message;
	private Instant instantStart;
	private Duration time;
	private String stageName;

	public Duration getTime() {
		return time;
	}

	public void setTime(Duration time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Instant getInstantStart() {
		return instantStart;
	}

	public void setInstantStart(Instant instantStart) {
		this.instantStart = instantStart;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SummaryStage))
			return false;
		SummaryStage that = (SummaryStage) o;
		return new EqualsBuilder().append(getMessage(), that.getMessage()).append(getInstantStart(), that.getInstantStart()).append(getStageName(), that.getStageName())
				.isEquals();
	}

	@Override public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getMessage()).append(getInstantStart()).append(getStageName()).toHashCode();
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("SummaryStage{");
		sb.append("message='").append(message).append('\'');
		sb.append(", instantStart=").append(instantStart);
		sb.append(", time=").append(time);
		sb.append(", stageName='").append(stageName).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
