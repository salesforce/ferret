/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties;

import com.datorama.exceptions.FerretException;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.io.*;
import java.util.Properties;

public class FilePropertiesService {
    private static final Logger log = Logger.getLogger(FilePropertiesService.class);


    public static Properties readProperties(File file) {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            log.warn("File was not found.", e);
            throw new FerretException(String.format("File not found -> %s.", file.toPath()), CommandLine.ExitCode.USAGE);
        } catch (IOException e) {
            log.warn("IO error.", e);
            throw new FerretException(String.format("Error with the file -> %s, error -> %s", file.toPath(), e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
        }
        return properties;
    }

    public static void writeProperties(File file, Properties properties) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.store(fileOutputStream, null);
        } catch (FileNotFoundException e) {
            log.warn("File was not found.", e);
            throw new FerretException(String.format("File not found -> %s.", file.toPath()), CommandLine.ExitCode.USAGE);
        } catch (IOException e) {
            log.warn("IO error.", e);
            throw new FerretException(String.format("Error with the file -> %s, error -> %s", file.toPath(), e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
        }
    }
}
