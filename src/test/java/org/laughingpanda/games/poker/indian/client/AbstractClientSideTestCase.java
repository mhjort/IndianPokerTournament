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
package org.laughingpanda.games.poker.indian.client;

import static org.easymock.EasyMock.createMock;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcHandler;
import org.laughingpanda.games.poker.indian.client.Bot;
import org.laughingpanda.games.poker.indian.client.PokerClient;
import org.laughingpanda.games.poker.indian.client.WebServers;


import junit.framework.TestCase;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public abstract class AbstractClientSideTestCase extends TestCase {

	protected static final String BOT_NAME = "Bob";

	protected static final int CLIENT_PORT = 5004;

	private static final int SERVER_PORT = 5003;

	protected String handlerName;

	protected Vector handlerArgs;

	protected Bot bot;

	@Override
	protected void setUp() throws Exception {
		startXmlRpcServerAtPort(SERVER_PORT);
		bot = createMock(Bot.class);
		PokerClient.join(localhost(), SERVER_PORT, BOT_NAME, bot, localhost(), CLIENT_PORT);
	}

	protected String localhost() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	private void startXmlRpcServerAtPort(int port) throws Exception {
		XmlRpcHandler handler = new XmlRpcHandler() {
			public Object execute(String name, Vector args) throws Exception {
				handlerName = name;
				handlerArgs = args;
				return Boolean.TRUE;
			}
		};

		WebServer server = WebServers.forPort(SERVER_PORT);
		server.removeHandler("Server");
		server.addHandler("Server", handler);
	}

}
