/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.common;

/**
 * nice data of emojis https://github.com/vdurmont/emoji-java/blob/master/src/main/resources/emojis.json
 */
public enum EmojiEnum {
	EMERGENCY("\uD83D\uDEA8"),
	GREEN_CHECK_MARK("@|bold,green \u2714|@"),
	RED_X_MARK("@|fg(160) \u2716|@"),
	NOTEBOOK("\uD83D\uDCD3"),
	EXCLAMATION_MARK("\u2757");

	private String value;

	EmojiEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
