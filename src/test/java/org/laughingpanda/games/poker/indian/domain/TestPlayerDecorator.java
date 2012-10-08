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
package org.laughingpanda.games.poker.indian.domain;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestPlayerDecorator extends TestCase {

	private Card card;

	private Player decorator;

	private PlayerProxy proxy;

	private String name;
	
	@Override
	protected void setUp() throws Exception {
		card = new Card(Suit.SPADES, 2);
		proxy = createMock(PlayerProxy.class);
		name = "Bob";
		decorator = new PlayerDecorator(name, proxy);
	}

	public void testDecoratorHoldsPlayersName() throws Exception {
		assertEquals(name, decorator.getName());
	}

	public void testDecoratorHoldsPlayersCard() throws Exception {
		decorator.setCard(card);
		assertEquals(card, decorator.getCard());
	}

	public void testDecoratorDoesNotShowCardToProxyUntilAsked() throws Exception {
		replay(proxy);
		decorator.setCard(card);
		verify(proxy);
		reset(proxy);
		proxy.showCard(card);
		replay(proxy);
		decorator.showCard();
		verify(proxy);
	}

	public void testDecoratorKeepsThePlayersStack() throws Exception {
		List<Player> otherPlayers = Arrays.asList(new Player[] { new MockPlayer() });
		decorator.tournamentBegins(100, otherPlayers);
		assertEquals(100, decorator.getStack());

		decorator.bet(20);
		assertEquals(80, decorator.getStack());
		assertTrue(decorator.hasChips());

		decorator.won(10);
		assertEquals(90, decorator.getStack());
		assertTrue(decorator.hasChips());

		decorator.bet(90);
		assertEquals(0, decorator.getStack());
		assertFalse(decorator.hasChips());
	}

	public void testStartOfHandIsDelegatedToProxy() throws Exception {
		MockPlayer p1 = new MockPlayer("P1");
		MockPlayer p2 = new MockPlayer("P2");
		MockPlayer p3 = new MockPlayer("Bob");
		Player dealer = p1;

		p1.setCard(new Card(Suit.CLUBS, 5));
		p2.setCard(new Card(Suit.CLUBS, 6));
		p3.setCard(new Card(Suit.CLUBS, 7));
		p1.setStack(40);
		p2.setStack(13);
		p3.setStack(60);

		Map<Player, Integer> blinds = new HashMap<Player, Integer>();
		blinds.put(p2, 1);
		blinds.put(p3, 2);

		Map<Player, Card> cards = new HashMap<Player, Card>();
		cards.put(p1, p1.getCard());
		cards.put(p2, p2.getCard());
		cards.put(p3, p3.getCard());

		Map<Player, Integer> stacks = new HashMap<Player, Integer>();
		stacks.put(p1, p1.getStack());
		stacks.put(p2, p2.getStack());
		stacks.put(p3, p3.getStack());

		proxy.tournamentStarts(anyInt(), (String[]) anyObject());
		proxy.handStarts(dealer, blinds, allBut(cards, "Bob"), stacks);
		replay(proxy);
		decorator.tournamentBegins(100, list(p1, p2, p3));
		decorator.handStarts(dealer, blinds);
		verify(proxy);
	}

	private Map<Player, Card> allBut(Map<Player, Card> cards, String playerWhoseCardIsRemoved) {
		Map<Player, Card> newCards = new HashMap<Player, Card>(cards);
		for (Player p : cards.keySet()) {
			if (p.getName().equals(playerWhoseCardIsRemoved)) {
				newCards.remove(p);
			}
		}
		return newCards;
	}

	private List<Player> list(Player... players) {
		return Arrays.asList(players);
	}

	public void testStartOfTournamentIsDelegatedToProxy() throws Exception {
		int stack = 100;
		List<Player> otherPlayers = Arrays.asList(new Player[] { new MockPlayer(name) });
		proxy.tournamentStarts(eq(stack), aryEq(new String[] { name }));
		replay(proxy);
		decorator.tournamentBegins(stack, otherPlayers);
		verify(proxy);
	}

	public void testPayingBlindIsDelegatedToProxy() throws Exception {
		int blind = 5;
		proxy.paidBlind(blind);
		replay(proxy);
		decorator.blind(blind);
		verify(proxy);
	}

	public void testDecoratorKeepsTrackOfPlayersTotalBetInCurrentHand() throws Exception {
		MockPlayer other1 = new MockPlayer("Other1");
		MockPlayer other2 = new MockPlayer("Other2");
		List<Player> otherPlayers = list(other1, other2);
		decorator.tournamentBegins(100, otherPlayers);

		decorator.handStarts(other1, new HashMap<Player, Integer>());
		assertEquals(0, decorator.totalBets());
		decorator.bet(4);
		decorator.bet(6);
		assertEquals(10, decorator.totalBets());
		decorator.handStarts(other2, new HashMap<Player, Integer>());
		assertEquals(0, decorator.totalBets());
	}

	public void testPlayerActionPromptIsDelegatedToProxy() throws Exception {
		int potSize = 50;
		int priceToCall = 10;
		int minRaise = 10;
		expect(proxy.action(priceToCall, minRaise, potSize)).andReturn(new Fold());
		proxy.performed(new Fold());
		replay(proxy);
		assertEquals(new Fold(), decorator.action(priceToCall, minRaise, potSize));
		verify(proxy);
	}

	public void testOtherPlayersActionsAreDelegatedToProxy() throws Exception {
		Fold action = new Fold();
		Player player = new MockPlayer();
		proxy.playerActed(player, action);
		replay(proxy);
		decorator.playerActed(new PlayerActionEvent(player, action, 0));
		verify(proxy);
	}

	public void testPlayerIsAllInWithBiggerRaiseThanStackWhenPriceToCallIsZero() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(0, 1, 0)).andReturn(new Raise(0, 6));
		proxy.performed(new Raise(0, 5));
		replay(proxy);
		assertEquals(new Raise(0, 5), decorator.action(0, 1, 0));
	}

	public void testRaiseWithMoreChipsThanStackButMoreThanEnoughToCallBecomesASmallerRaise() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(4, 1, 0)).andReturn(new Raise(4, 6));
		proxy.performed(new Raise(4, 1));
		replay(proxy);
		assertEquals(new Raise(4, 1), decorator.action(4, 1, 0));
	}

	public void testRaiseWithExactlyTheRightAmountOfChips() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(2, 1, 0)).andReturn(new Raise(2, 3));
		proxy.performed(new Raise(2, 3));
		replay(proxy);
		assertEquals(new Raise(2, 3), decorator.action(2, 1, 0));
	}

	public void testRaiseWithJustEnoughChipsForCall() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(5, 1, 0)).andReturn(new Raise(5, 3));
		proxy.performed(new Call(5));
		replay(proxy);
		assertEquals(new Call(5), decorator.action(5, 1, 0));
	}

	public void testCheckIsChangedToCallWhenThePlayerNeedsToPayOrFold() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(2, 1, 0)).andReturn(new Check());
		proxy.performed(new Call(2));
		replay(proxy);
		assertEquals(new Call(2), decorator.action(2, 1, 0));
	}

	public void testPlayerRaiseIsDelegatedToProxy() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(0, 1, 0)).andReturn(new Raise(0, 4));
		proxy.performed(new Raise(0, 4));
		replay(proxy);
		assertEquals(new Raise(0, 4), decorator.action(0, 1, 0));
	}

	public void testDecoratorAddsThePriceToCallIntoRaiseAction() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		Raise botAction = new Raise(1, 4);
		expect(proxy.action(1, 1, 0)).andReturn(botAction);
		proxy.performed(botAction);
		replay(proxy);
		assertEquals(new Raise(1, 4), decorator.action(1, 1, 0));
	}

	public void testRaiseBiggerThanStackIsChangedToCall() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(6, 1, 0)).andReturn(new Raise(6, 1));
		proxy.performed(new Call(5));
		replay(proxy);
		assertEquals(new Call(5), decorator.action(6, 1, 0));
	}

	public void testRaiseLessThanMinimumIsRaisedToMinimum() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(0, 3, 0)).andReturn(new Raise(0, 1));
		proxy.performed(new Raise(0, 3));
		replay(proxy);
		assertEquals(new Raise(0, 3), decorator.action(0, 3, 0));
	}

	public void testRaiseLessThanMinimumWithLessStackThanMinimumIsAllIn() throws Exception {
		decorator.tournamentBegins(5, new ArrayList<Player>());
		expect(proxy.action(0, 6, 0)).andReturn(new Raise(0, 2));
		proxy.performed(new Raise(0, 5));
		replay(proxy);
		assertEquals(new Raise(0, 5), decorator.action(0, 6, 0));
	}
}
