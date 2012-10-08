package org.laughingpanda.games.poker.indian.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author Markus Hjort
 */
public class ConfigurationReader {
	public Configuration readConfiguration(InputStream in) {
		LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(in));
		String botName = showQuestionAndReadAnswer(lineReader, "What is the name of your bot?");
		String className = showQuestionAndReadAnswer(lineReader,
				"What is the class name of your Bot (note, it should have default constructor)?");
		Host serverHost = showQuestionsAndReadHost(lineReader, "What is the server's hostname?", "What is the server's port number?");
		Host botHost = showQuestionsAndReadHost(lineReader, "What is your IP address or hostname?", "What port you want to run your client in?");
		return new Configuration(botName, className, serverHost, botHost);
	}

	private Host showQuestionsAndReadHost(LineNumberReader lineReader, String hostNameQuestion, String hostPort) {
		String serverHostName = showQuestionAndReadAnswer(lineReader, hostNameQuestion);
		int serverPort = Integer.valueOf(showQuestionAndReadAnswer(lineReader, hostPort));
		Host host = new Host(serverHostName, serverPort);
		return host;
	}

	private String showQuestionAndReadAnswer(LineNumberReader lineReader, String question) {
		System.out.println(question);
		try {
			return lineReader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
