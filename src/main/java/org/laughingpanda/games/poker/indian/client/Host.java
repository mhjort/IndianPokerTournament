package org.laughingpanda.games.poker.indian.client;

/**
 * @author Markus Hjort
 */
public class Host {
	private String name;
	private int port;
	
	public Host(String name, int port) {
		this.name = name;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}
}
