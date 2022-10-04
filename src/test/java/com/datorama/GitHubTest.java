/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.datorama.models.RemoteConfig;

public class GitHubTest {
	@Test
	public void downloadTest() throws IOException {
//		String token = System.getenv("HOMEBREW_GITHUB_API_TOKEN");
//		GithubHttpClient githubHttpClient = new GithubHttpClient("", token);
//		Optional<List<RepositoryFile>> repositoryFileList = githubHttpClient.repositoryFileList("datorama",
//				"tubatura", "", "master");
//		File fileTo = File.createTempFile("test", ".tmp");
//		repositoryFileList.get().stream().forEach(System.out::println);
//		githubHttpClient.downloadFile(repositoryFileList.get().stream().filter(repositoryFile -> repositoryFile.getName().equals("README.md")).findFirst().get().getDownloadURL(), fileTo);
//		Files.readAllLines(fileTo.toPath()).forEach(System.out::println);
	}

	@Test
	public void testRemote() {
		RemoteConfig remote = new RemoteConfig();
		remote.setOwner("com/datorama");
		remote.setRepository("pilot");
		remote.setBranch("master");
		remote.setFile("wow.yaml");
		System.out.println(remote.getFilePathInLocal());
		System.out.println(remote.getShaFilePathInLocal());
		System.out.println(remote.getFileName());
		System.out.println(remote.getPathBeforeFileInRepository());
		System.out.println(remote.getFileNameSuffix());
	}
}
