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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.laughingpanda.games.poker.indian.domain.PlayerProxy;
import org.laughingpanda.games.poker.indian.server.Server;
import org.laughingpanda.games.poker.indian.server.XmlRpcConnector;
import org.laughingpanda.games.poker.indian.server.XmlRpcPlayerProxy;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestXmlRpcConnector extends TestCase {

	private static final int SERVER_PORT = 5501;

	private static final int CLIENT_PORT = 5502;

	private static final String HOSTNAME = "localhost";

	public PlayerProxy proxyCreatedByConnector;

	protected int botReceivedPaidBlind;

	private CountDownLatch fakeBotReceivedCallFromServer;

	@Override
	protected void setUp() throws Exception {
		fakeBotReceivedCallFromServer = new CountDownLatch(1);
		SingletonXmlRpcServer.setHandler("Bot", new Handler());
	}

	public void testXmlRpcConnectorJoinsXmlRpcProxiesToServer() throws Exception {
		MockServer server = new MockServer();

		XmlRpcConnector connector = new XmlRpcConnector(HOSTNAME, SERVER_PORT);
		connector.connectTo(server);
		connector.start();

		sendJoinCallToServer();
		assertNotNull("'join' message should've resulted in a call to Server#join()", proxyCreatedByConnector);
		assertEquals(XmlRpcPlayerProxy.class, proxyCreatedByConnector.getClass());
	}

	private void sendJoinCallToServer() throws MalformedURLException, XmlRpcException, IOException {
		Vector<Comparable> args = new Vector<Comparable>();
		args.add(HOSTNAME);
		args.add(CLIENT_PORT);
		args.add("Bob");
		XmlRpcClient client = new XmlRpcClient(HOSTNAME, SERVER_PORT);
		assertEquals(true, client.execute(XmlRpcConnector.HANDLER_NAME + ".join", args));
	}

	public class Handler implements XmlRpcHandler {

		public Object execute(String handlerName, Vector args) throws Exception {
			botReceivedPaidBlind = Integer.parseInt(String.valueOf(args.get(0)));
			fakeBotReceivedCallFromServer.countDown();
			return Boolean.TRUE;
		}

	}

	private class MockServer extends Server {
		@Override
		public void join(PlayerProxy proxy) {
			proxyCreatedByConnector = proxy;
		}
	}
}
