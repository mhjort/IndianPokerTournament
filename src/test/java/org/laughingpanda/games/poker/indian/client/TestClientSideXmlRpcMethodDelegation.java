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

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestClientSideXmlRpcMethodDelegation extends AbstractClientSideTestCase {

	public void testShouldDelegateTournamentStarts() throws Exception {
		int initialStack = 25;
		String[] playerNames = { "player1", "player2" };
		bot.tournamentStarts(eq(initialStack), aryEq(playerNames));
		replay(bot);
		executeAndVerify("Bot.tournamentStarts", initialStack, playerNames);
	}

	public void testShouldDelegateHandStarts() throws Exception {
		String dealer = "dealer";
		Hashtable<String, Integer> blinds = new Hashtable<String, Integer>();
		blinds.put("player", 1);
		Hashtable<String, String> cards = new Hashtable<String, String>();
		cards.put("player", "S2");
		Hashtable<String, Integer> stacks = new Hashtable<String, Integer>();
		stacks.put("player", 50);
		bot.handStarts(dealer, blinds, cards, stacks);
		replay(bot);
		executeAndVerify("Bot.handStarts", dealer, blinds, cards, stacks);
	}

	public void testShouldDelegatePlayerActed() throws Exception {
		String player = "player";
		String action = "fold";
		bot.playerActed(player, action);
		replay(bot);
		executeAndVerify("Bot.playerActed", player, action);
	}

	public void testShouldDelegateAction() throws Exception {
		int priceToCall = 5;
		int minimumRaise = 1;
		int potSize = 100;
		expect(bot.action(priceToCall, minimumRaise, potSize)).andReturn("fold");
		replay(bot);
		assertEquals("fold", executeAndVerify("Bot.action", priceToCall, minimumRaise, potSize));
	}

	public void testShouldDelegateShowCard() throws Exception {
		String card = "S2";
		bot.showCard(card);
		replay(bot);
		executeAndVerify("Bot.showCard", card);
	}

	public void testShouldDelegatePaidBlind() throws Exception {
		int chips = 12;
		bot.paidBlind(chips);
		replay(bot);
		executeAndVerify("Bot.paidBlind", chips);
	}

	public void testShouldDelegatePerformed() throws Exception {
		String performedAction = "fold";
		bot.performed(performedAction);
		replay(bot);
		executeAndVerify("Bot.performed", performedAction);
	}

	public void testShouldDelegateWon() throws Exception {
		int chips = 100;
		bot.won(chips);
		replay(bot);
		executeAndVerify("Bot.won", chips);
	}

	private Object executeAndVerify(String rpcMethodName, Object... args) throws Exception {
		Vector argsVector = new Vector();
		argsVector.addAll(Arrays.asList(args));
		Object result = new XmlRpcClient(localhost(), CLIENT_PORT).execute(rpcMethodName, argsVector);
		verify(bot);
		return result;
	}
}
