/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.rest.github;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.datorama.models.github.RepositoryFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;

public class GithubHttpClient {
	private static final String GITHUB_URL = "https://api.github.com/";
	private final Logger log = (Logger) LoggerFactory.getLogger(GithubHttpClient.class);
	private final String username;
	private final String token;
	private final ObjectMapper objectMapper;

	public GithubHttpClient(String username, String token) {
		this.username = username;
		this.token = token;
		objectMapper = new ObjectMapper();
	}

	public Optional<List<RepositoryFile>> repositoryFileList(String owner, String repo, String path, String branch) {
		HttpURLConnection con = null;
		try {
			String urlString = GITHUB_URL + "repos/" + owner + "/" + repo + "/contents" + path + "?ref=" + branch;
			URL url = new URL(urlString);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(20000);
			con.setReadTimeout(20000);
			addHeaders(con);
			InputStream inputStream = con.getInputStream();
			return Optional.of(objectMapper.readValue(inputStream
					, objectMapper.getTypeFactory().constructCollectionType(List.class, RepositoryFile.class)));
		} catch (IOException e) {
			log.warn("Failed IO/URI.", e.getMessage());
		} finally {
			con.disconnect();
		}
		return Optional.empty();
	}

	private String encodedAuthToken() {
		String toBase64 = username + ":" + token;
		return Base64.getEncoder().encodeToString((toBase64).getBytes());
	}

	private void addHeaders(HttpURLConnection request) {
		request.setRequestProperty("Authorization", "Basic " + encodedAuthToken());
		request.setRequestProperty("User-Agent", "node.js");
		request.setRequestProperty("Content-Type", "");
	}

	public boolean downloadFile(String downloadUrl, File file) {
		try(BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream())) {
			Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			log.warn("Failed IO/URI.", e.getMessage());
		}
		return false;
	}
}
