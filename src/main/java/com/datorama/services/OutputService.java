/*
 *  Copyright (c) 2021, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 *
 */

package com.datorama.services;

import com.datorama.exceptions.FerretException;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class OutputService {
	private static OutputService outputService;
	private static final Logger log = Logger.getLogger(OutputService.class);
	private PrintStream out;

	private OutputService() {
		//Deny init
	}

	public static OutputService getInstance() {
		if (outputService == null) {
			synchronized (OutputService.class) {
				if (outputService == null) {
					outputService = new OutputService();
					outputService.initialize();
				}
			}
		}
		return outputService;
	}

	private void initialize() {
		try {
			out = new PrintStream(System.out,true,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.warn("unsupported encoding",e);
			throw new FerretException("Failed encoding for output stream", CommandLine.ExitCode.SOFTWARE);
		}
	}

	public void header1(String value) {
		String ansiString = CommandLine.Help.Ansi.AUTO.string("@|bold,green " + value + "|@");
		out.println(ansiString);
	}

	public String greenBold(String value){
		return CommandLine.Help.Ansi.AUTO.string("@|bold,green " + value + "|@");
	}

	public void header2(String value) {
		String ansiString = CommandLine.Help.Ansi.AUTO.string("@|bold,fg(33) " + value + "|@");
		out.println(ansiString);
	}

	public void error(String value) {
		String ansiString = CommandLine.Help.Ansi.AUTO.string("@|bold,fg(160) " + value + "|@");
		out.println(ansiString);
	}

	public String redBold(String value){
		return CommandLine.Help.Ansi.AUTO.string("@|fg(160) " + value + "|@");
	}

	public void normal(String value) {
		String ansiString = CommandLine.Help.Ansi.AUTO.string(value);
		out.println(ansiString);
	}

	public String boldPart(String value) {
		return "@|bold " + value + "|@";
	}
}
