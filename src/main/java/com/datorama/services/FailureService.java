/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.datorama.models.OnFailure;

import ch.qos.logback.classic.Logger;

/**
 * Service to handle failure events received from pipeline.
 */
public class FailureService {
	private final Logger log = (Logger) LoggerFactory.getLogger(FailureService.class);
	private static FailureService failureService;
	private String message;


	private FailureService() {
		//Deny init
	}

	public static FailureService getInstance() {
		if (failureService == null) {
			synchronized (FailureService.class) {
				if (failureService == null) {
					failureService = new FailureService();
				}
			}
		}
		return failureService;
	}

	public String getMessage() {
		if (StringUtils.isEmpty(message)) {
			return "";
		}
		return message;
	}

	public void setFailure(OnFailure failure) {
		if(ObjectUtils.isEmpty(failure)){
			this.message = "";
		} else {
			this.message = failure.getMessage();
		}
	}
}
