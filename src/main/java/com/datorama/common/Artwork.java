/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.common;

public enum Artwork {

    PIPELINE_ARTWORK("############################################################\n" +
            "#\n" +
            "#  ____        _                                  \n" +
            "# |  _ \\  __ _| |_ ___  _ __ __ _ _ __ ___   __ _ \n" +
            "# | | | |/ _` | __/ _ \\| '__/ _` | '_ ` _ \\ / _` |\n" +
            "# | |_| | (_| | || (_) | | | (_| | | | | | | (_| |\n" +
            "# |____/ \\__,_|\\__\\___/|_|  \\__,_|_| |_| |_|\\__,_|\n" +
            "#                                  ON LOCALHOST\n" +
            "#\n" +
            "#                                  powered by ferret\n" +
            "############################################################");
    private String value;

    Artwork(String message) {
        this.value = message;
    }


    public String getValue() {
        return value;
    }
}
