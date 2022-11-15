/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.common.EmojiEnum;
import com.datorama.models.Summary;
import com.datorama.models.SummaryStage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class SummaryService {
    static final Logger log = Logger.getLogger(SummaryService.class);
    List<SummaryStage> summaryStageList;
    Instant startTime;

    @Inject
    ReplaceKeysService replaceKeysService;
    @Inject
    OutputService outputService;


    @PostConstruct
    void initialize() {
        summaryStageList = new CopyOnWriteArrayList<>();
        setStartTime();
    }

    public void setStartTime() {
        if (ObjectUtils.isEmpty(startTime)) {
            this.startTime = Clock.systemDefaultZone().instant();
        }
    }

    private Instant getStartTime() {
        return startTime;
    }


    public SummaryStage startOfStage(Summary summary, String stageName, Map<String, String> inputMap) {
        SummaryStage summaryStage = new SummaryStage();
        summaryStage.setStageName(stageName);
        summaryStage.setInstantStart(Clock.systemDefaultZone().instant());
        if (ObjectUtils.isEmpty(summary) || StringUtils.isEmpty(summary.getMessage())) {
            summaryStage.setMessage("");
        } else {
            String message = replaceKeysService.replaceKeysInLine(summary.getMessage(), inputMap);
            summaryStage.setMessage(message);
        }
        summaryStageList.add(summaryStage);
        return summaryStage;
    }

    public void endOfStage(SummaryStage summaryStage) {
        Instant endTime = Clock.systemDefaultZone().instant();
        int size = summaryStageList.size();
        for (int i = size - 1; i >= 0; i--) {
            SummaryStage stage = summaryStageList.get(i);
            if (stage.equals(summaryStage)) {
                stage.setTime(Duration.between(stage.getInstantStart(), endTime));
                break;
            }
        }
    }

    public void summaryMessage(boolean isPassed) {
        StringBuilder stringBuilder = new StringBuilder();
        summaryStageList.forEach(summaryStage -> {
            if (isPassed || ObjectUtils.isNotEmpty(summaryStage.getTime())) {
                stringBuilder.append(EmojiEnum.GREEN_CHECK_MARK.getValue())
                        .append(" ");
                stringBuilder.append(summaryStage.getTime().getSeconds()).append("s ");
            } else {
                stringBuilder.append(EmojiEnum.RED_X_MARK.getValue())
                        .append(" ");
            }
            stringBuilder.append(summaryStage.getStageName()).append(System.lineSeparator());

        });
        if (isPassed && !summaryStageList.isEmpty()) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(EmojiEnum.NOTEBOOK.getValue()).append(EmojiEnum.NOTEBOOK.getValue()).append(EmojiEnum.NOTEBOOK.getValue());
            stringBuilder.append(" Summary additional information ");
            stringBuilder.append(EmojiEnum.NOTEBOOK.getValue()).append(EmojiEnum.NOTEBOOK.getValue()).append(EmojiEnum.NOTEBOOK.getValue());
            stringBuilder.append(System.lineSeparator());
            summaryStageList.forEach(summaryStage -> {
                if (StringUtils.isNotEmpty(summaryStage.getMessage())) {
                    stringBuilder.append(summaryStage.getStageName()).append(": ").append(summaryStage.getMessage()).append(System.lineSeparator());
                }
            });
        }
        outputService.normal(stringBuilder.toString());
    }
}
