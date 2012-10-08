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

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestClientSideXmlRpcClient extends AbstractClientSideTestCase {

	public void testPlayerCanJoinTournamentOnServer() throws Exception {
		assertServerHasReceivedJoin(BOT_NAME);
	}

	public void testPlayerJoinStartsXlmRpcServer() throws Exception {
		int chips = 12;
		bot.paidBlind(chips);
		replay(bot);
		Vector args = new Vector();
		args.add(chips);
		new XmlRpcClient(localhost(), CLIENT_PORT).execute("Bot.paidBlind", args);
		verify(bot);
	}

	private void assertServerHasReceivedJoin(String playerName) throws Exception {
		assertEquals("Server.join", handlerName);
		Vector expectedArgs = new Vector();
		expectedArgs.add(localhost().toString());
		expectedArgs.add(CLIENT_PORT);
		expectedArgs.add(BOT_NAME);
		assertEquals(expectedArgs, handlerArgs);
	}
}
