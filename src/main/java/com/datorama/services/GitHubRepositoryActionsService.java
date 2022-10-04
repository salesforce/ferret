/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.exceptions.FerretException;
import com.datorama.models.CredentialsProvider;
import com.datorama.models.ProcessResponse;
import com.datorama.models.RepositoryProvider;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class GitHubRepositoryActionsService {
	private static GitHubRepositoryActionsService gitActionsService;
	private static final Logger log = Logger.getLogger(GitHubRepositoryActionsService.class);
	private final ProcessService processService = ProcessService.getInstance();
	private final GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();

	private GitHubRepositoryActionsService() {
		//Deny init
	}

	public static GitHubRepositoryActionsService getInstance() {
		if (gitActionsService == null) {
			synchronized (GitHubRepositoryActionsService.class) {
				if (gitActionsService == null) {
					gitActionsService = new GitHubRepositoryActionsService();
				}
			}
		}
		return gitActionsService;
	}

	public void clone(Path repoToCloneIn, RepositoryProvider repositoryProvider, CredentialsProvider credentialsProvider) {
		OutputService.getInstance().normal("Cloning " + repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository() + "...");
		globalDirectoryService.createDirectoryInFerretDir(repoToCloneIn);
		String gitCloneCommand = fullGitCloneCommand(repositoryProvider, credentialsProvider);
		Optional<ProcessResponse> processResponseOptional = processService.runCommand(gitCloneCommand, repoToCloneIn);
		ProcessResponse processResponse = processResponseOptional.get();
		if (processResponse.getExitCode() != 0) {
			log.warn("Failed in cloning error message: " + processResponse.getOutput().orElse(""));
			throw new FerretException("Failed cloning repository " + repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository(), CommandLine.ExitCode.SOFTWARE);
		}
		OutputService.getInstance().normal("Finished cloning " + repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository());
	}

	public void checkIfGitExist(Path repoToCloneIn) {
		Optional<ProcessResponse> processResponseOptional = processService.runCommand("git version", repoToCloneIn);
		int exitValue = processResponseOptional.get().getExitCode();
		if (exitValue != 0) {
			log.warn("Failed in checking git cli exists. message: " + processResponseOptional.get().getOutput().orElse(""));
			throw new FerretException("git command line was not found. please install", CommandLine.ExitCode.USAGE);
		}
	}

	private String fullGitCloneCommand(RepositoryProvider repositoryProvider, CredentialsProvider credentialsProvider) {
		return "git clone --branch " + repositoryProvider.getBranch() + " --single-branch " + httpsGitHubURL(repositoryProvider, credentialsProvider);
	}

	private String httpsGitHubURL(RepositoryProvider repositoryProvider, CredentialsProvider credentialsProvider) {
		return "https://" + credentialsProvider.getGithubToken() + ":x-oauth-basic@github.com/"
				+ repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository() + ".git";
	}

	public void pull(Path directoryOfRepositoryOfFerretGitRepositories, RepositoryProvider repositoryProvider, CredentialsProvider credentialsProvider) throws FerretException {
		Path clonedRepositoryDir = clonedRepositoryDirPath(directoryOfRepositoryOfFerretGitRepositories, repositoryProvider);
		OutputService.getInstance().normal("Pulling " + repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository() + "...");
		boolean httpsURLIsCorrect = httpsURLIsCorrect(clonedRepositoryDir, repositoryProvider, credentialsProvider);
		boolean isBranchCorrect = checkBranchIsCorrect(clonedRepositoryDir, repositoryProvider);
		if (!httpsURLIsCorrect || !isBranchCorrect) {
			FileService.deleteDirectoryAndAllFilesInside(directoryOfRepositoryOfFerretGitRepositories);
			clone(directoryOfRepositoryOfFerretGitRepositories, repositoryProvider, credentialsProvider);
		}
		Optional<ProcessResponse> processResponseOptional = processService.runCommand("git pull", clonedRepositoryDir);
		if (processResponseOptional.get().getExitCode() != 0) {
			log.warn("Failed pulling message: " + processResponseOptional.get().getOutput().orElse(""));
			OutputService.getInstance().normal("Failed pulling repository " + repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository() + ", but allowing to keep running pipeline.");
			//			throw new FerretException("Failed pulling repository " +  repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository(), CommandLine.ExitCode.SOFTWARE);
		}
		OutputService.getInstance().normal("Finished pulling " + repositoryProvider.getOwner() + "/" + repositoryProvider.getRepository());
	}

	public Path clonedRepositoryDirPath(Path directoryOfRepositoryOfFerretGitRepositories, RepositoryProvider repositoryProvider) {
		return Paths.get(directoryOfRepositoryOfFerretGitRepositories.toString(), repositoryProvider.getRepository());
	}

	/**
	 * check https url for token,owner and repository
	 * if it's token only, will replace it. and return true
	 * if it's owner or repository will return false for clone.
	 *
	 * @param directoryOfRepository
	 * @param repositoryProvider
	 * @param credentialsProvider
	 * @return
	 * @throws FerretException
	 */
	private boolean httpsURLIsCorrect(Path directoryOfRepository, RepositoryProvider repositoryProvider, CredentialsProvider credentialsProvider)  {
		Optional<ProcessResponse> gitGetUrlOptional = null;
		gitGetUrlOptional = processService.runCommand("git config --get remote.origin.url", directoryOfRepository);
		String output = gitGetUrlOptional.get().getOutput().orElse("");
		if (output.equals(httpsGitHubURL(repositoryProvider, credentialsProvider))) {
			log.debug("git origin url the same, meaning no change needed.");
		} else {
			log.debug("git origin url changed. so setting a new one");
			if (!output.contains(repositoryProvider.getOwner()) || !output.contains(repositoryProvider.getRepository())) {
				log.debug("git origin url changed. new owner or repository -> " + repositoryProvider.toString());
				return false;
			}
			log.debug("git origin url changed. only token has changed so setting a new one");
			Optional<ProcessResponse> gitSetUrlOptional = processService.runCommand("git remote set-url origin " + httpsGitHubURL(repositoryProvider, credentialsProvider), directoryOfRepository);
			if (gitSetUrlOptional.get().getExitCode() != 0) {
				log.warn("Failed updating origin url message: " + gitSetUrlOptional.get().getOutput().orElse(""));
				throw new FerretException("Your token was changed, ferret tried to update url to include your token but failed."
						+ " please do it manually for now, by going to " + directoryOfRepository.toString() + " and changing the url (in config of git) to use your token.",
						CommandLine.ExitCode.SOFTWARE);
			}
		}
		return true;
	}

	private boolean checkBranchIsCorrect(Path directoryOfRepository, RepositoryProvider repositoryProvider) {
		Optional<ProcessResponse> gitGetFetchOptional;
		try {
			gitGetFetchOptional = processService.runCommand("git config --get remote.origin.fetch", directoryOfRepository);
		} catch (FerretException e) {
			return false;
		}
		String output = gitGetFetchOptional.get().getOutput().orElse("");
		if (output.isEmpty()) {
			log.debug("output was empty for checking branch");
			return false;
		}
		int lastSlash = output.trim().lastIndexOf("/");
		String branch = output.substring(lastSlash + 1);
		if (branch.equals(repositoryProvider.getBranch())) {
			log.debug("Repository branch didn't change.");
			return true;
		}
		log.warn("branch is different requested: " + branch + " but use: " + output);
		return false;
	}

	public boolean existingCredentials(CredentialsProvider credentialsProvider) {
		return !StringUtils.isAnyEmpty(credentialsProvider.getGithubUsername(), credentialsProvider.getGithubToken());
	}
}
