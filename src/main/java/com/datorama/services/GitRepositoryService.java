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
import com.datorama.models.RepositoryProvider;
import com.datorama.services.pipelines.PipelineProvider;
import com.datorama.services.pipelines.PipelinesRepositoryService;
import com.datorama.services.properties.DirectoryProperties;
import com.datorama.services.properties.FilePropertiesService;
import com.datorama.services.properties.RepositoryProperties;
import com.datorama.services.properties.directory.GenericDirectoryProperties;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

public class GitRepositoryService {
	private static GitRepositoryService gitRepositoryService;
	private static final Logger log = Logger.getLogger(GitRepositoryService.class);
	private final CredentialsService credentialsService = CredentialsService.getInstance();
	private final GitHubRepositoryActionsService githubActionsService = GitHubRepositoryActionsService.getInstance();
	private final DirectoryProperties directoryProperties = DirectoryProperties.getInstance();
	private final PipelinesRepositoryService pipelinesRepositoryService = PipelinesRepositoryService.getInstance();

	private GitRepositoryService() {
		//Deny init
	}

	public static GitRepositoryService getInstance() {
		if (gitRepositoryService == null) {
			synchronized (GitRepositoryService.class) {
				if (gitRepositoryService == null) {
					gitRepositoryService = new GitRepositoryService();
					gitRepositoryService.initialize();
				}
			}
		}
		return gitRepositoryService;
	}

	private void initialize() {
		Optional<RepositoryProvider> repositoryProviderOptional = RepositoryProperties.getProperties();
		//validation if user input repository details at all
		if (!repositoryProviderOptional.isPresent() || !validInformationForRepositoryActions(repositoryProviderOptional.get())) {
			OutputService.getInstance().normal(OutputService.getInstance().boldPart("Missing git repository settings, please set them to get more of ferret. get help by typing: ferret repository help "));
			return;
			}
		//validation if credentials were entered
		if (!githubActionsService.existingCredentials(credentialsService.getCredentialsProvider())) {
			throw new FerretException("Missing credentials settings. help by: ferret credentials help ", CommandLine.ExitCode.USAGE);
		}
		
			githubActionsService.checkIfGitExist(Paths.get(Constants.USER_HOME_PATH));
		
		//check if directory does not exist or empty
		if (!Files.exists(Constants.GIT_REPO_DIR)) {
			try {
				githubActionsService.clone(Constants.GIT_REPO_DIR, repositoryProviderOptional.get(), credentialsService.getCredentialsProvider());
			} catch (FerretException e) {
				log.warn("failed cloning so cleaning the directories / files downloaded.", e);
				FileService.deleteDirectoryAndAllFilesInside(Constants.GIT_REPO_DIR);
				throw new FerretException(e.getMessage(), e.getExitCode());
			}
		} else {
			try {
				githubActionsService.pull(Constants.GIT_REPO_DIR, repositoryProviderOptional.get(), credentialsService.getCredentialsProvider());
			} catch (FerretException e) {
				log.warn("failed pulling", e);
				throw new FerretException(e.getMessage(), e.getExitCode());
			}
		}
		Path clonedRepositoryDirectory = githubActionsService.clonedRepositoryDirPath(Constants.GIT_REPO_DIR, repositoryProviderOptional.get());
		validRepositoryHierarchy(clonedRepositoryDirectory);
		addDirectoriesProperties(clonedRepositoryDirectory);
		addPipelines(clonedRepositoryDirectory);
	}

	/**
	 * Add pipelines to runetime
	 *
	 * @param clonedRepositoryDirectory
	 */
	private void addPipelines(Path clonedRepositoryDirectory) {
		Path pipelinesDir = Paths.get(clonedRepositoryDirectory.toString(), "pipelines");
		try {
			Files.walk(pipelinesDir)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(file -> {
						if (!file.isDirectory()) {
							if (file.getName().contains(".yaml") || file.getName().contains(".yml")) {
								PipelineProvider pipelineProvider = new PipelineProvider();
								pipelineProvider.setPath(file.toPath());
								String fileInRepo = file.getAbsolutePath().substring(pipelinesDir.toString().length() + 1);
								pipelineProvider.setFileName(fileInRepo);
								int dotOccurrence = fileInRepo.lastIndexOf(".");
								String keyPrefix = fileInRepo.substring(0, dotOccurrence);
								pipelineProvider.setKeyPrefix(keyPrefix);
								log.debug("added pipeline -> " + pipelineProvider.toString());
								pipelinesRepositoryService.addPipeline(pipelineProvider);
							}
						}
					});
		} catch (IOException e) {
			log.warn("Failed adding properties", e);
			throw new FerretException("Failed adding properties to ferret from common repository.", CommandLine.ExitCode.SOFTWARE);
		}
	}

	/**
	 * Add properties files from repository to be used in ferret in pipelines.
	 *
	 * @param clonedRepositoryDirectory
	 */
	private void addDirectoriesProperties(Path clonedRepositoryDirectory) {
		Path propertiesDir = Paths.get(clonedRepositoryDirectory.toString(), "properties");
		try {
			Files.walk(propertiesDir)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(file -> {
						if (!file.isDirectory()) {
							if (file.getName().contains(".properties")) {
								GenericDirectoryProperties genericDirectoryProperties = new GenericDirectoryProperties();
								genericDirectoryProperties.setFilePath(file);
								genericDirectoryProperties.setProperties(FilePropertiesService.readProperties(file));
								String fileName = file.getAbsolutePath().substring(propertiesDir.toAbsolutePath().toString().length() + 1);
								genericDirectoryProperties.setFileName(fileName);
								int lastDotOccurrence = file.getAbsolutePath().lastIndexOf(".");
								int lastSlashOccurrence = file.getAbsolutePath().lastIndexOf("/");
								String filePrefix = file.getAbsolutePath().substring(lastSlashOccurrence + 1, lastDotOccurrence + 1);
								genericDirectoryProperties.setKeyPrefix(filePrefix);
								log.debug("added " + genericDirectoryProperties.toString() + " to properties for ferret pipelines.");
								directoryProperties.add(genericDirectoryProperties);
							}
						}
					});
		} catch (IOException e) {
			log.warn("Failed adding properties", e);
			throw new FerretException("Failed adding properties to ferret from common repository.", CommandLine.ExitCode.SOFTWARE);
		}
	}

	public boolean validInformationForRepositoryActions(RepositoryProvider repositoryProvider) {
		return !StringUtils.isAnyEmpty(repositoryProvider.getRepository(), repositoryProvider.getOwner(), repositoryProvider.getBranch());
	}

	/**
	 * Validation of repository hierarchy.
	 *
	 * @param repositoryClonedPath
	 * @return
	 */
	private void validRepositoryHierarchy(Path repositoryClonedPath) {
		Path pipelinesDir = Paths.get(repositoryClonedPath.toString(), "pipelines");
		if (!Files.exists(pipelinesDir)) {
			throw new FerretException("Ferret common repository need to be in a specific hierarchy, searched for pipelines directory and it was not found. Searched: " + pipelinesDir.toAbsolutePath().toString(),
							CommandLine.ExitCode.USAGE);
		}
		Path propertiesDir = Paths.get(repositoryClonedPath.toString(), "properties");
		if (!Files.exists(propertiesDir)) {
			throw new FerretException("Ferret common repository need to be in a specific hierarchy, searched for properties directory and it was not found. Searched: " + propertiesDir.toAbsolutePath().toString(),
							CommandLine.ExitCode.USAGE);
		}
	}
}
