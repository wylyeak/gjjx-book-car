package org.wylyeak.gjjx;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.xml.DOMConfigurator;

public class Main {

	public static void main(String[] args) throws ParseException,
			ClientProtocolException, IOException {
		DOMConfigurator.configure("config/log4j.xml");
		Option optUsername = new Option("u", true, "login username");
		optUsername.setRequired(true);
		Option optPassword = new Option("p", true, "login password");
		optPassword.setRequired(true);
		Option optFileName = new Option("jpg", true, "save code jpg");
		optFileName.setRequired(true);
		Option optDate = new Option("d", true, "book date");
		optDate.setRequired(true);
		Option optTime = new Option("t", true, "book time");
		optTime.setRequired(true);
		Options options = new Options();
		options.addOption(optUsername);
		options.addOption(optPassword);
		options.addOption(optFileName);
		options.addOption(optDate);
		options.addOption(optTime);
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);
		String userName = cmd.getOptionValue("u");
		String password = cmd.getOptionValue("p");
		String fileName = cmd.getOptionValue("jpg");
		String date = cmd.getOptionValue("d");
		String time = cmd.getOptionValue("t");
		if (userName != null && password != null) {
			AutoBookCarHandler handler = new AutoBookCarHandler(userName,
					password, fileName, date, time);
			Thread thread = new Thread(handler);
			thread.start();
		} else {
			System.out.println("opt error");
		}
	}
}
