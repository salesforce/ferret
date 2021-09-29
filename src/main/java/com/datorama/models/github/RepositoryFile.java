/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.models.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class RepositoryFile {
	private String name;
	@JsonProperty(value = "download_url",defaultValue = "")
	private String downloadURL;
	private String path;
	private String sha;
	private long size;
	private String url;
	@JsonProperty("git_url")
	private String gitURL;
	@JsonProperty("html_url")
	private String htmlURL;
	private String type;
	@JsonProperty("_links")
	private Links links;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public long getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getGitURL() {
		return gitURL;
	}

	public void setGitURL(String gitURL) {
		this.gitURL = gitURL;
	}

	public String getHtmlURL() {
		return htmlURL;
	}

	public void setHtmlURL(String htmlURL) {
		this.htmlURL = htmlURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("DownloadFile{");
		sb.append("name='").append(name).append('\'');
		sb.append(", downloadURL='").append(downloadURL).append('\'');
		sb.append(", path='").append(path).append('\'');
		sb.append(", sha='").append(sha).append('\'');
		sb.append(", size=").append(size);
		sb.append(", url='").append(url).append('\'');
		sb.append(", gitURL='").append(gitURL).append('\'');
		sb.append(", htmlURL='").append(htmlURL).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append(", links=").append(links.toString());
		sb.append('}');
		return sb.toString();
	}
}
