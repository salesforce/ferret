/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.exceptions.FerretException;
import com.datorama.files.FerretYamlFile;
import com.datorama.services.properties.ManagerPropertiesService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RewriteYamlService {
    static final Logger log = Logger.getLogger(RewriteYamlService.class);
    ManagerPropertiesService managerPropertiesService = ManagerPropertiesService.getInstance();
    @Inject
    FindKeysService findKeysService;
    ReplaceKeysService replaceKeysService = ReplaceKeysService.getInstance();

    public FerretYamlFile rewriteFerretYamlFile(File yamlFile, Map<String, String> rewriteKeys) throws FerretException {
        FileService.isFileExist(yamlFile);
        List<String> keysInFile = findKeysService.getKeys(yamlFile);
        keysInFile.removeAll(rewriteKeys.keySet());
        Map<String, String> properties = managerPropertiesService.getPropertiesToRewrite(keysInFile, yamlFile.toPath());
        properties.putAll(rewriteKeys);
        File rewriteYamlFile = replaceKeysService.rewriteLines(yamlFile, properties);
        return (FerretYamlFile) YamlFileService.loadYamlAs(rewriteYamlFile, FerretYamlFile.class);
    }
}
