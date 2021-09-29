/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import java.io.*;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.nodes.Tag;

import com.datorama.exceptions.FerretException;

import ch.qos.logback.classic.Logger;
import picocli.CommandLine;

public class YamlFileService {
	private static final Logger log = (Logger) LoggerFactory.getLogger(YamlFileService.class);

	public static Object loadYamlAs(File file, Class clz) {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			Yaml yaml = new Yaml(new Constructor(clz));
			Object object = yaml.load(fileInputStream);
			if (ObjectUtils.isEmpty(object)) {
				throw new FerretException(String.format("File is invalid -> %s.", file.toPath()), CommandLine.ExitCode.USAGE);
			}
			return object;
		} catch (FileNotFoundException e) {
			log.warn("File was not found.", e);
			throw new FerretException(String.format("File not found -> %s.", file.toPath()), CommandLine.ExitCode.USAGE);
		} catch (IOException e) {
			log.warn("IO error.", e);
			throw new FerretException(String.format("Error with the file -> %s, error -> %s", file.toPath(), e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (MarkedYAMLException e) {
			log.warn("yaml parse failed.", e);
			throw new FerretException(String.format("Failed parsing the yaml at %s", file.toPath()) + System.lineSeparator() + e.getProblem() + " " + e.getProblemMark(), CommandLine.ExitCode.USAGE);
		}
	}

	public static Map<String, Object> loadYamlAsProperties(File file)  {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			Yaml yaml = new Yaml();
			Map<String, Object> map = yaml.load(fileInputStream);
			return map;
		} catch (FileNotFoundException e) {
			log.warn("File was not found.", e);
			throw new FerretException(String.format("File not found -> %s.", file.toPath()), CommandLine.ExitCode.USAGE);
		} catch (IOException e) {
			log.warn("IO error.", e);
			throw new FerretException(String.format("Error with the file -> %s, error -> %s", file.toPath(), e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		} catch (ConstructorException e) {
			log.warn("yaml parse failed.", e);
			throw new FerretException(String.format("Failed parsing the yaml at %s", file.toPath()) + System.lineSeparator() + e.getProblemMark(), CommandLine.ExitCode.USAGE);
		}
	}

	public static void dumpToYaml(Object data, File file) {
		String yamlString;
		DumperOptions options = new DumperOptions();
		options.setPrettyFlow(true);
		options.setIndent(2);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);
		yamlString = yaml.dumpAs(data, Tag.MAP, null);
		log.debug(String.format("Dump yaml data -> %s", yamlString));
		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(yamlString);
			fileWriter.flush();
		} catch (IOException e) {
			log.warn("IO error.", e);
			throw new FerretException(String.format("Error with the file -> %s, error -> %s", file.toPath(), e.getLocalizedMessage()), CommandLine.ExitCode.SOFTWARE);
		}
	}
}
