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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcHandler;
import org.laughingpanda.games.poker.indian.domain.Card;
import org.laughingpanda.games.poker.indian.domain.MockPlayer;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.Suit;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 * @author Paolo Perrotta
 */
public class TestXmlRpcPlayerProxy extends TestCase implements XmlRpcHandler {

	private static final long LATCH_TIMEOUT_SECONDS = 10;

	private Map<String, Vector> botInvocations;

	private Map<String, Object> botReturnValues;

	private XmlRpcPlayerProxy proxy;

	private CountDownLatch botWasCalled;

	@Override
	protected void setUp() throws Exception {
		botInvocations = new HashMap<String, Vector>();
		botReturnValues = new HashMap<String, Object>();
		botWasCalled = new CountDownLatch(1);

		SingletonXmlRpcServer.setHandler("Bot", this);
		proxy = new XmlRpcPlayerProxy("Bot", InetAddress.getLocalHost().getHostAddress(), SingletonXmlRpcServer.PORT);
	}

	public Object execute(String handlerName, Vector args) throws Exception {
		botInvocations.put(handlerName, args);
		botWasCalled.countDown();
		if (!botReturnValues.containsKey(handlerName)) {
			return Boolean.TRUE;
		}
		return botReturnValues.get(handlerName);
	}

	public void testPlayerName() throws Exception {
		assertEquals("Bot", proxy.getName());
	}

	public void testPaidBlind() throws Exception {
		int chips = 25;
		proxy.paidBlind(chips);
		assertInvocation("Bot.paidBlind", chips);
	}

	public void testWonChips() throws Exception {
		int chips = 25;
		proxy.won(chips);
		assertInvocation("Bot.won", chips);
	}

	public void testHandStarts() throws Exception {
		MockPlayer p1 = new MockPlayer("P1");
		MockPlayer p2 = new MockPlayer("P2");
		MockPlayer p3 = new MockPlayer("P3");
		Player dealer = p1;
		p1.setCard(new Card(Suit.CLUBS, 5));
		p2.setCard(new Card(Suit.CLUBS, 6));
		p3.setCard(new Card(Suit.CLUBS, 7));
		p1.setStack(10);
		p2.setStack(20);
		p3.setStack(30);

		Map<Player, Integer> blinds = new Hashtable<Player, Integer>();
		blinds.put(p2, 2);
		blinds.put(p3, 4);

		Map<Player, Card> cards = new Hashtable<Player, Card>();
		cards.put(p1, p1.getCard());
		cards.put(p2, p2.getCard());
		cards.put(p3, p3.getCard());

		Map<Player, Integer> stacks = new Hashtable<Player, Integer>();
		stacks.put(p1, p1.getStack());
		stacks.put(p2, p2.getStack());
		stacks.put(p3, p3.getStack());

		proxy.handStarts(dealer, blinds, cards, stacks);

		Hashtable<String, Integer> proxyBlinds = new Hashtable<String, Integer>();
		proxyBlinds.put(p2.getName(), 2);
		proxyBlinds.put(p3.getName(), 4);

		Hashtable<String, String> proxyCards = new Hashtable<String, String>();
		proxyCards.put(p1.getName(), p1.getCard().toString());
		proxyCards.put(p2.getName(), p2.getCard().toString());
		proxyCards.put(p3.getName(), p3.getCard().toString());

		Hashtable<String, Integer> proxyStacks = new Hashtable<String, Integer>();
		proxyStacks.put(p1.getName(), p1.getStack());
		proxyStacks.put(p2.getName(), p2.getStack());
		proxyStacks.put(p3.getName(), p3.getStack());

		assertInvocation("Bot.handStarts", dealer.getName(), proxyBlinds, proxyCards, proxyStacks);
	}

    public void testUnderstandsFolds() throws Exception {
        setReturnValue("Bot.action", "fold");
        assertEquals(new Fold(), proxy.action(1, 1, 1));
        assertInvocation("Bot.action", 1, 1, 1);
    }

    public void testUnderstandsChecks() throws Exception {
        setReturnValue("Bot.action", "check");
        assertEquals(new Check(), proxy.action(1, 1, 2));
        assertInvocation("Bot.action", 1, 1, 2);
    }

    public void testUnderstandsCalls() throws Exception {
        setReturnValue("Bot.action", "call");
        assertEquals(new Call(1), proxy.action(1, 1, 3));
        assertInvocation("Bot.action", 1, 1, 3);
    }

    public void testUnderstandsRaises() throws Exception {
        setReturnValue("Bot.action", "raise by 3");
        assertEquals(new Raise(1, 3), proxy.action(1, 1, 4));
        assertInvocation("Bot.action", 1, 1, 4);

        setReturnValue("Bot.action", "raise by 40");
        assertEquals(new Raise(1, 40), proxy.action(1, 1, 5));
        assertInvocation("Bot.action", 1, 1, 5);
    }

    public void testIllegalPlayerActionsBecomeFolds() {
        setReturnValue("Bot.action", "raise by 1.2");
        assertEquals(new Fold(), proxy.action(1, 1, 1));
    }
    
	public void testTournamentStarts() throws Exception {
		int stack = 100;
		String[] seatingOrder = new String[] { "Al", "Bob" };
		proxy.tournamentStarts(stack, seatingOrder);

		Vector<String> rpcSeatingOrder = new Vector<String>();
		rpcSeatingOrder.addAll(Arrays.asList(seatingOrder));
		assertInvocation("Bot.tournamentStarts", stack, rpcSeatingOrder);
	}

	public void testPerformedAction() throws Exception {
		proxy.performed(new Call(3));
		assertInvocation("Bot.performed", "call");

		proxy.performed(new Check());
		assertInvocation("Bot.performed", "check");

		proxy.performed(new Fold());
		assertInvocation("Bot.performed", "fold");

		proxy.performed(new Raise(0, 4));
		assertInvocation("Bot.performed", "raise by 4");
	}

	public void testPlayerActed() throws Exception {
		Player player = new MockPlayer("Someone");

		proxy.playerActed(player, new Fold());
		assertInvocation("Bot.playerActed", player.getName(), "fold");

		proxy.playerActed(player, new Call(2));
		assertInvocation("Bot.playerActed", player.getName(), "call");

		proxy.playerActed(player, new Check());
		assertInvocation("Bot.playerActed", player.getName(), "check");

		proxy.playerActed(player, new Raise(0, 4));
		assertInvocation("Bot.playerActed", player.getName(), "raise by 4");
	}

	public void testShowCard() throws Exception {
		Card card = new Card(Suit.HEARTS, 2);
		proxy.showCard(card);
		assertInvocation("Bot.showCard", card.toString());
	}

	private void setReturnValue(String name, Object value) {
		botReturnValues.put(name, value);
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
