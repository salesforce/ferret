/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.pipelines;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PipelinesRepositoryService {
    static final Logger log = Logger.getLogger(PipelinesRepositoryService.class);
    List<PipelineProvider> pipelineProviders;


    @PostConstruct

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
