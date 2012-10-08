package org.laughingpanda.games.poker.indian.client;

/**
 * @author Markus Hjort
 */
public class Configuration {
	private String botName;
	private String botClass;
	private Host serverHost;
	private Host botHost;
	
	public Configuration(String botName, String botClass, Host serverHost, Host botHost) {
		this.botName = botName;
		this.botClass = botClass;
		this.serverHost = serverHost;
		this.botHost = botHost;
	}

	public String getBotClass() {
		return botClass;
	}

	public Host getBotHost() {
		return botHost;
	}

	public String getBotName() {
		return botName;
	}

	public Host getServerHost() {
		return serverHost;
	}
}
