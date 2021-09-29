/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.pipelines;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.datorama.services.GitHubRepositoryActionsService;

import ch.qos.logback.classic.Logger;

public class PipelinesRepositoryService {
	private static PipelinesRepositoryService pipelinesRepositoryService;
	private final Logger log = (Logger) LoggerFactory.getLogger(PipelinesRepositoryService.class);
	private final GitHubRepositoryActionsService gitHubRepositoryActionsService = GitHubRepositoryActionsService.getInstance();
	private List<PipelineProvider> pipelineProviders;

	public static PipelinesRepositoryService getInstance() {
		if (pipelinesRepositoryService == null) {
			synchronized (PipelinesRepositoryService.class) {
				if (pipelinesRepositoryService == null) {
					pipelinesRepositoryService = new PipelinesRepositoryService();
					pipelinesRepositoryService.init();
				}
			}
		}
		return pipelinesRepositoryService;
	}

	private void init() {
		pipelineProviders = new ArrayList<>();
	}
	public List<PipelineProvider> pipelines() {
		return pipelineProviders;
	}

	public void addPipeline(PipelineProvider pipelineProvider) {
		pipelineProviders.add(pipelineProvider);
	}
}
