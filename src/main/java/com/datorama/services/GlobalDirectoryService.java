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
import com.datorama.services.properties.directory.UserPropertiesService;
import org.codehaus.plexus.util.FileUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.datorama.common.Constants.*;

/**
 * Manage ferret hidden directory and global.
 */
@ApplicationScoped
public class GlobalDirectoryService {
    static final Logger log = Logger.getLogger(GlobalDirectoryService.class);
    @Inject
    CredentialsService credentialsService;

    @Inject
    OutputService outputService;
    @Inject
    UserPropertiesService userPropertiesService;

    @PostConstruct
    public void initialize() {
        if (!Files.exists(FERRET_DIR)) {
            log.debug("Initialize main ferret directory at " + FERRET_DIR);
            try {
                Files.createDirectory(FERRET_DIR);
                credentialsService.createFile();
                userPropertiesService.createDefaultProperties();
            } catch (IOException e) {
                log.warn("Failed initializing .ferret directory", e);
                throw new FerretException("Failed initializing .ferret directory.", CommandLine.ExitCode.SOFTWARE);
            }
        }
    }

    public File fileInTempDirectory(String fileName) {
        createDirectoryInFerretDir(Constants.FERRET_TEMP_DIR);
        Path filePath = Paths.get(Constants.FERRET_TEMP_DIR.toString(), fileName);
        if (!Files.exists(filePath)) {
            log.debug("Creating temp file at " + filePath);
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                log.warn("Failed creating temp file", e);
                throw new FerretException("Failed creating temp file.", CommandLine.ExitCode.SOFTWARE);
            }
        }
        return filePath.toFile();
    }

    public void createFileInSystem(Path file) {
        createDirectoryInFerretDir(file.getParent());
        createFile(file);
    }

    public void createDirectoryInFerretDir(Path path) {
        {
            if (!Files.exists(path)) {
                log.debug("Creating directory at " + path);
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    log.warn("Failed creating directory", e);
                    throw new FerretException("Failed creating directory.", CommandLine.ExitCode.SOFTWARE);
                }
            }
        }
    }

    private File createFile(Path fileDir) {
        log.debug("Creating file at " + fileDir.toString());
        File file = new File(fileDir.toString());
        file.setReadable(true);
        file.setWritable(true);
        file.setExecutable(true);
        try {
            file.createNewFile();
        } catch (IOException e) {
            log.warn("Failed creating file", e);
            throw new FerretException("Failed creating file.", CommandLine.ExitCode.SOFTWARE);
        }
        return file;
    }

    public void deleteSubFolder() {
        try {
            outputService.normal("deleting.ferret/remote folder");
            FileUtils.deleteDirectory(new File(REMOTE_DIR.toString()));
            outputService.normal("deleting.ferret/tmp folder");
            FileUtils.deleteDirectory(new File(FERRET_TEMP_DIR.toString()));
        } catch (IOException e) {
            log.warn("Failed deleting .ferret/remote or .ferret/temp folder", e);
            throw new FerretException("Failed deleting .ferret folder", CommandLine.ExitCode.SOFTWARE);
        }
    }
}
