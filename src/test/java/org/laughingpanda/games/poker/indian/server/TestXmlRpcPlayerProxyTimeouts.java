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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcHandler;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.server.XmlRpcPlayerProxy;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestXmlRpcPlayerProxyTimeouts extends TestCase implements XmlRpcHandler {

	private static final long LATCH_TIMEOUT_SECONDS = 10;

	private Map<String, Vector> botInvocations;

	private XmlRpcPlayerProxy proxy;

	private CountDownLatch botWasCalled;

	@Override
	protected void setUp() throws Exception {
		botInvocations = new HashMap<String, Vector>();
		botWasCalled = new CountDownLatch(1);

		SingletonXmlRpcServer.setHandler("Bot", this);
		proxy = new XmlRpcPlayerProxy("Bot", InetAddress.getLocalHost().getHostAddress(), SingletonXmlRpcServer.PORT);
	}

	public Object execute(String handlerName, Vector args) throws Exception {
		botInvocations.put(handlerName, args);
		botWasCalled.countDown();
		Thread.sleep(10000);
		return Boolean.FALSE;
	}

	public void testProxyHasTimeout() throws Exception {
		long time = System.currentTimeMillis();
		proxy.paidBlind(1);
		time = System.currentTimeMillis() - time;
		assertInvocation("Bot.paidBlind", 1);
		assertTrue("Long-lasting proxy calls should return after timeout", time < 5500);
	}

	public void testTimeoutForActionPromptMeansFold() throws Exception {
		assertEquals(new Fold(), proxy.action(1, 2, 3));
	}

	private void assertInvocation(String botHandlerName, Object... args) {
		waitFor(botWasCalled);
		Vector<Object> expected = new Vector<Object>();
		for (Object arg : args) {
			expected.add(arg);
		}
		assertEquals(expected, botInvocations.get(botHandlerName));
	}

	private static void waitFor(CountDownLatch latch) {
		try {
			assertTrue("Joined proxy didn't communicate back within timeout period", latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			fail("Latch wait was interrupted during the timeout period");
		}
	}

}
