/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.common.Constants;
import com.datorama.exceptions.FerretException;
import org.codehaus.plexus.util.StringUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Replace values in a file.
 */
@ApplicationScoped
public class ReplaceKeysService {
    static final Logger log = Logger.getLogger(ReplaceKeysService.class);
    @Inject
    GlobalDirectoryService globalDirectoryService;


    public File rewriteLines(File file, Map<String, String> keyValues) {
        List<String> lines = LineReaderService.readLines(file);
        List<String> replacedLines = new ArrayList<>();
        for (int i = 0; i <= lines.size() - 1; i++) {
            replacedLines.add(replaceKeysInLine(lines.get(i), keyValues));
        }
        String[] splitFileName = file.getName().split("\\.");
        String tempFileName = splitFileName[0] + "temp";
        String typeFileName = splitFileName[1];
        String fileName = tempFileName + "." + typeFileName;
        File fileTemp = globalDirectoryService.fileInTempDirectory(fileName);
        try {
            Files.write(fileTemp.toPath(), replacedLines);
        } catch (IOException e) {
            log.warn("Failed writing to file.", e);
            throw new FerretException(String.format("Failed writing to file %s.", file.getAbsolutePath()), CommandLine.ExitCode.SOFTWARE);
        }
        return fileTemp;
    }

    /**
     * get key in dynamic value when there is dynamic value.
     *
     * @param line - line to check for dynamic value.
     * @return key if there is one.
     */
    public String replaceKeysInLine(String line, Map<String, String> keyValues) {
        List<Integer> startIndices = LineReaderService.findIndicesOfWordInLine(line, Constants.START_DYNAMIC_VAR);
        if (startIndices.size() == 0) {
            return line;
        }
        List<Integer> endIndices = LineReaderService.findIndicesOfWordInLine(line, Constants.END_DYNAMIC_VAR);
        if (startIndices.size() != endIndices.size()) {
            return line;
        }
        for (int i = startIndices.size() - 1; i >= 0; i--) {
            int startIndex = startIndices.get(i);
            int endIndex = endIndices.get(i);
            String key = LineReaderService.getValueInLine(startIndex, endIndex, line);
            String value = keyValues.get(key);
            String toReplace = line.substring(startIndex, endIndex + Constants.END_DYNAMIC_VAR.length());
            if (StringUtils.isNotEmpty(value)) {
                log.debug(String.format("Replace %s with %s", toReplace, value));
                line = line.replace(toReplace, value);
                log.debug("generated line: " + line);
            }
        }
        return line;
    }
}
