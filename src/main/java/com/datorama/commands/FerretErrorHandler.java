/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.commands;

import com.datorama.common.EmojiEnum;
import com.datorama.exceptions.FerretException;
import com.datorama.services.ExitService;
import com.datorama.services.FailureService;
import com.datorama.services.SummaryService;
import org.apache.commons.lang3.StringUtils;

public interface FerretErrorHandler {
	default void ferretRun(Runnable runnable) {
		try {
			runnable.run();
			SummaryService.getInstance().summaryMessage(true);
		} catch (FerretException ferretException) {
			SummaryService.getInstance().summaryMessage(false);
			String message = FailureService.getInstance().getMessage();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(ferretException.getMessage()).append(System.lineSeparator());
			if (StringUtils.isNotEmpty(message)) {
				stringBuilder.append(EmojiEnum.EXCLAMATION_MARK.getValue())
						.append(EmojiEnum.EXCLAMATION_MARK.getValue())
						.append(EmojiEnum.EXCLAMATION_MARK.getValue())
						.append(" ")
						.append(message)
						.append(EmojiEnum.EXCLAMATION_MARK.getValue())
						.append(EmojiEnum.EXCLAMATION_MARK.getValue())
						.append(EmojiEnum.EXCLAMATION_MARK.getValue())
						.append(System.lineSeparator());
			}
			ExitService.getInstance().exit(stringBuilder.toString(), ferretException.getExitCode());
		}
	}
}
