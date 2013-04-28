package org.wylyeak.gjjx;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.client.ClientProtocolException;

public class Main {

	public static void main(String[] args) throws ParseException,
			ClientProtocolException, IOException {
		Option optUsername = new Option("u", true, "login username");
		optUsername.setRequired(true);
		Option optPassword = new Option("p", true, "login password");
		optPassword.setRequired(true);
		Option optFileName = new Option("jpg", true, "save code jpg");
		optFileName.setRequired(true);
		Options options = new Options();
		options.addOption(optUsername);
		options.addOption(optPassword);
		options.addOption(optFileName);
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);
		String userName = cmd.getOptionValue("u");
		String password = cmd.getOptionValue("p");
		String fileName = cmd.getOptionValue("jpg");
		if (userName != null && password != null) {
			AutoBookCarHandler handler = new AutoBookCarHandler(userName,
					password, fileName);
			Thread thread = new Thread(handler);
			thread.start();
		} else {
			System.out.println("opt error");
		}
	}
}
