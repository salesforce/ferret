/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.datorama.exceptions.FerretException;
import com.datorama.models.CredentialsProvider;
import com.datorama.models.RemoteConfig;
import com.datorama.models.github.RepositoryFile;
import com.datorama.rest.github.GithubHttpClient;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

public class RemoteService {
	private static RemoteService remoteService;
	private final Logger log = (Logger) LoggerFactory.getLogger(RemoteService.class);
	private final CredentialsService credentialsService = CredentialsService.getInstance();
	private final GlobalDirectoryService globalDirectoryService = GlobalDirectoryService.getInstance();
	private GithubHttpClient githubHttpClient;

	private RemoteService() {
	}

	public static RemoteService getInstance() {
		if (remoteService == null) {
			synchronized (RemoteService.class) {
				if (remoteService == null) {
					remoteService = new RemoteService();
					remoteService.initialize();
				}
			}
		}
		return remoteService;
	}

	private void initialize() {
		CredentialsProvider credentialsProvider = credentialsService.getCredentialsProvider();
		githubHttpClient = new GithubHttpClient(credentialsProvider.getGithubUsername(), credentialsProvider.getGithubToken());
	}

	public File remoteFile(RemoteConfig remote) {
		validate(remote);
		//fetching directory in repository of the file requested
		Optional<List<RepositoryFile>> repositoryFileList = githubHttpClient.repositoryFileList(remote.getOwner(), remote.getRepository(), remote.getPathBeforeFileInRepository(), remote.getBranch());
		Optional<RepositoryFile> repositoryFileOptional = Optional.empty();
		if (repositoryFileList.isPresent()) {
			log.debug("got repository of files list");
			repositoryFileOptional = repositoryFileList.get().stream().filter(repositoryFile -> repositoryFile.getName().equals(remote.getFileName())).findFirst();
		}
		if (Files.exists(remote.getFilePathInLocal())) {
			log.debug("File already exists");
			String oldSha = getOldSha(remote);
			if (repositoryFileOptional.isPresent()) {
				if (repositoryFileOptional.get().getSha().equals(oldSha)) {
					//file has not changed so use one already in local.
					log.debug("the file has not changed");
					return remote.getFilePathInLocal().toFile();
				} else {
					//file has changed, downloading the newer version.
					Optional<File> downloadedNewerFile = downloadNewerVersion(remote, repositoryFileOptional);
					if (downloadedNewerFile.isPresent())
						return downloadedNewerFile.get();
				}
			}
			OutputService.getInstance().error("Failed downloading " + remote.getFile() + " but there is one in local, will use it.");
			return remote.getFilePathInLocal().toFile();
		}
		//first time downloading this file
		if (repositoryFileOptional.isPresent()) {
			log.debug("Fetching this file for first time.");
			Optional<File> downloadedFile = downloadVersion(remote, repositoryFileOptional);
			if (downloadedFile.isPresent()){
				return downloadedFile.get();
			}
			throw new FerretException("Failed in downloading file " + remote.getFileName(), CommandLine.ExitCode.SOFTWARE);
		}
		//if didn't found the file name in the path given in repo so handle better error message
		if (repositoryFileList.isPresent() && !repositoryFileOptional.isPresent()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			repositoryFileList.get().forEach(repositoryFile -> sb.append(repositoryFile.getName()).append(","));
			sb.append("]");
			throw new FerretException("File requested " + remote.getFile() + " is not found in the repository, Ferret found " +sb.toString()+ " in the repository path given " + remote.getPathBeforeFileInRepository(), CommandLine.ExitCode.USAGE);
		}
		//could have failed cause of status code different 200 in the request or bad data given by user.
		throw new FerretException("Could not download file and did not find old download of it locally. If file located in private repository,"
								+ " please make sure you added correct credentials (with credentials command)."
								+ " Make sure remote data is correct - owner,repository,branch,file (need to include path if file not in root of repository).",
						CommandLine.ExitCode.USAGE);
	}

	private Optional<File> downloadVersion(RemoteConfig remote, Optional<RepositoryFile> repositoryFileOptional) {
		globalDirectoryService.createFileInSystem(remote.getFilePathInLocal());
		boolean downloaded = githubHttpClient.downloadFile(repositoryFileOptional.get().getDownloadURL(), remote.getFilePathInLocal().toFile());
		if (downloaded) {
			long sizeFile = 0;
			try {
				sizeFile = Files.size(remote.getFilePathInLocal());
				if (sizeFile == repositoryFileOptional.get().getSize()) {
					Files.write(remote.getShaFilePathInLocal(), repositoryFileOptional.get().getSha().getBytes());
					return Optional.of(remote.getFilePathInLocal().toFile());
				}
			} catch (IOException e) {
				log.warn("Failed fetching size of file", e);
			}
		}
		return Optional.empty();
	}

	private Optional<File> downloadNewerVersion(RemoteConfig remote, Optional<RepositoryFile> repositoryFileOptional) {
		File tempFile = null;
		long fileToDownloadSize = repositoryFileOptional.get().getSize();
		try {
			tempFile = File.createTempFile(remote.getFileName(), ".tmp");
			tempFile.deleteOnExit();
			Files.write(tempFile.toPath(), Files.readAllBytes(remote.getFilePathInLocal()));
			remote.getFilePathInLocal().toFile().delete();
			globalDirectoryService.createFileInSystem(remote.getFilePathInLocal());
			boolean downloaded = githubHttpClient.downloadFile(repositoryFileOptional.get().getDownloadURL(), remote.getFilePathInLocal().toFile());
			if (downloaded) {
				long sizeFile = Files.size(remote.getFilePathInLocal());
				if (fileToDownloadSize == sizeFile) {
					Files.write(remote.getShaFilePathInLocal(), repositoryFileOptional.get().getSha().getBytes());
				} else {
					Files.write(remote.getFilePathInLocal(), Files.readAllBytes(tempFile.toPath()));
				}
				return Optional.of(remote.getFilePathInLocal().toFile());
			} else {
				Files.write(remote.getFilePathInLocal(), Files.readAllBytes(tempFile.toPath()));
			}
		} catch (IOException e) {
			log.warn("Failed writing content in file", e);
			long sizeTemp;
			long sizeFile;
			try {
				sizeFile = Files.size(remote.getFilePathInLocal());
				if (sizeFile > 0) {
					return Optional.of(remote.getFilePathInLocal().toFile());
				}
				sizeTemp = Files.size(tempFile.toPath());
				if (sizeTemp > 0) {
					Files.write(remote.getFilePathInLocal(), Files.readAllBytes(tempFile.toPath()));
				}
			} catch (IOException ioException) {
				log.warn("Failed fetching size of file", e);
			}
		}
		return Optional.empty();
	}

	private String getOldSha(RemoteConfig remote) {
		try {
			return Files.readAllLines(remote.getShaFilePathInLocal()).get(0);
		} catch (IOException e) {
			log.warn("Failed reading content in sha file", e);
		}
		return "";
	}

	private void validate(RemoteConfig remote) {
		if (StringUtils.isEmpty(remote.getOwner()) || StringUtils.isEmpty(remote.getBranch()) || StringUtils.isEmpty(remote.getFile()) || StringUtils.isEmpty(remote.getRepository())) {
			log.debug("Missing data for remote action");
			throw new FerretException("Missing data to get file from remote. Ferret needs: owner,repository,branch and file (path if not in root of repository).", CommandLine.ExitCode.USAGE);
		}
	}
}
