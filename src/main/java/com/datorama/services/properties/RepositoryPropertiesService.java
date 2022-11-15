/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services.properties;

import com.datorama.common.Constants;
import com.datorama.models.RepositoryProvider;
import com.datorama.services.GlobalDirectoryService;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

@ApplicationScoped
public class RepositoryPropertiesService {
	public static final String REPOSITORY_NAME = "repository";
	public static final String OWNER_NAME = "owner";
	public static final String BRANCH_NAME = "branch";

	@Inject
	GlobalDirectoryService globalDirectoryService;
	private static String getFileName() {
		return "repository.properties";
	}

	public static File getFile() {
		return Paths.get(Constants.FERRET_DIR.toString(), getFileName()).toFile();
	}

	public  Optional<RepositoryProvider> getProperties() {
		if (!Files.exists(getFile().toPath())) {
			globalDirectoryService.createFileInSystem(getFile().toPath());
		}
		Properties current = FilePropertiesService.readProperties(getFile());
		RepositoryProvider repositoryProvider = new RepositoryProvider();
		String repositoryName = current.getProperty(REPOSITORY_NAME);
		if(StringUtils.isEmpty(repositoryName)){
			return Optional.empty();
		}
		repositoryProvider.setRepository(repositoryName);
		repositoryProvider.setBranch(current.getProperty(BRANCH_NAME));
		repositoryProvider.setOwner(current.getProperty(OWNER_NAME));
		return Optional.of(repositoryProvider);
	}

	public  void addProperties(RepositoryProvider repositoryProvider) {
		if (!Files.exists(getFile().toPath())) {
			globalDirectoryService.createFileInSystem(getFile().toPath());
		}
		Properties current = FilePropertiesService.readProperties(getFile());
		current.setProperty(REPOSITORY_NAME, repositoryProvider.getRepository());
		current.setProperty(OWNER_NAME, repositoryProvider.getOwner());
		current.setProperty(BRANCH_NAME, repositoryProvider.getBranch());
		FilePropertiesService.writeProperties(getFile(),current);
	}

}
