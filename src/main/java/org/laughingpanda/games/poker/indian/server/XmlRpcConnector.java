/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laughingpanda.games.poker.indian.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.xmlrpc.WebServer;
import org.laughingpanda.games.poker.indian.domain.PlayerProxy;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class XmlRpcConnector implements Connector {

	public static final String HANDLER_NAME = "Server";

	private final String host;

	private final int port;

	private Server server;

	public XmlRpcConnector(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void connectTo(Server server) {
		this.server = server;
	}

	public void start() {
		try {
			WebServer rpcServer = new WebServer(port, InetAddress.getByName(host));
			rpcServer.start();
			rpcServer.addHandler(HANDLER_NAME, new XmlRpcHandler());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public PlayerProxy createPlayerProxyFor(String name, String address, int port) {
		return new XmlRpcPlayerProxy(name, address, port);
	}

	/**
	 * A separate class/object for handling the XML-RPC requests. The
	 * XML-RPC server will map incoming requests to public methods in this
	 * class based on the method name and the types of arguments.
	 */
	public class XmlRpcHandler {
		public Object join(String host, int port, String name) {
			System.out.println("Player \"" + name + "\" from " + host + ":" + port + " joining the tournament...");
			server.join(createPlayerProxyFor(name, host, port));
			return Boolean.TRUE;
		}
	}

}
