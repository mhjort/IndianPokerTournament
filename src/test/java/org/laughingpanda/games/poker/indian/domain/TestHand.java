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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;
import org.laughingpanda.games.poker.indian.domain.events.BlindEvent;
import org.laughingpanda.games.poker.indian.domain.events.HandEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerPromptEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerWinEvent;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestHand extends TestCase implements HandListener {

	private MockPlayer player1, player2, player3;

	private Hand hand;

	private int smallBlind, bigBlind;

	private Action shouldNotBeCalledAction;

	private List<HandEvent> actualEvents;

	private List<Player> players;

	@Override
	protected void setUp() throws Exception {
		smallBlind = 1;
		bigBlind = 2;
		actualEvents = new ArrayList<HandEvent>();

		player1 = new MockPlayer("P1");
		player2 = new MockPlayer("P2");
		player3 = new MockPlayer("P3");
		players = Arrays.asList(new Player[] { player1, player2, player3 });

		shouldNotBeCalledAction = new Fold();

		FixedDeck deck = new FixedDeck();
		deck.add(new Card(Suit.CLUBS, 1));
		deck.add(new Card(Suit.CLUBS, 2));
		deck.add(new Card(Suit.CLUBS, 3));

		hand = new Hand();
		hand.setBlinds(smallBlind, bigBlind);
		hand.setPlayers(players);
		hand.setDealer(player1);
		hand.setDeck(deck);
	}

	public void testShouldNotCallLastPlayerWhenOthersHaveFolded() throws Exception {
		player1.stack = 5;
		player2.stack = 9;
		player3.stack = 9;
		player1.actions.add(new Fold());
		player2.actions.add(new Fold());
		player3.actions.add(shouldNotBeCalledAction);
		hand.play();
		assertSame(shouldNotBeCalledAction, player3.actions.poll());
	}

	public void testCallWithLessChipsThanCalledForDoesNotCreateMoneyFromNowhere() throws Exception {
		player1.stack = 20;
		player2.stack = 5;
		player1.actions.add(new Raise(0, 10));
		player2.actions.add(new Call(10));
		hand.play();
		assertEquals(5, player2.totalBets());
	}

	public void testOnePlayerDoesntHaveChipsAfterPayingHisBlinds() throws Exception {
		player1.stack = 10;
		player2.stack = 1;
		player3.stack = 10;
		hand.play();
		assertEquals(3, player3.getChipsWon());
		assertEquals(1, player2.totalBets());
	}

	public void testHandNotifiesListenersOfEventsDuringHand() throws Exception {
		player1.stack = 5;
		player2.stack = 5;
		player3.stack = 5;
		player1.actions.add(new Fold());
		int player2Raise = 2;
		player2.actions.add(new Raise(smallBlind, player2Raise));
		player3.actions.add(new Call(player2Raise));
		hand.addListener(this);

		hand.play();

		List<HandEvent> expectedHandEvents = new ArrayList<HandEvent>();
		expectedHandEvents.add(new DummyEvent("hand starts", player1, player2, player3));

		expectedHandEvents.add(new BlindEvent(player2, smallBlind));
		expectedHandEvents.add(new BlindEvent(player3, bigBlind));

		expectedHandEvents.add(new PlayerPromptEvent(player1, bigBlind, bigBlind, smallBlind + bigBlind));
		PlayerActionEvent p1Folds = new PlayerActionEvent(player1, new Fold(), 0);
		expectedHandEvents.add(p1Folds);

		expectedHandEvents.add(new PlayerPromptEvent(player2, smallBlind, bigBlind, smallBlind + bigBlind));
		PlayerActionEvent p2Raises = new PlayerActionEvent(player2, new Raise(smallBlind, player2Raise), smallBlind + player2Raise);
		expectedHandEvents.add(p2Raises);

		expectedHandEvents.add(new PlayerPromptEvent(player3, player2Raise, bigBlind, (2 * bigBlind) + player2Raise));
		PlayerActionEvent p3Calls = new PlayerActionEvent(player3, new Call(player2Raise), player2Raise);
		expectedHandEvents.add(p3Calls);

		expectedHandEvents.add(new DummyEvent("showdown"));
		expectedHandEvents.add(new PlayerWinEvent(player3, (2 * bigBlind) + player2Raise + player2Raise));
		expectedHandEvents.add(new DummyEvent("hand ends"));
		assertEquals(expectedHandEvents, actualEvents);

		player1.assertReceivedPlayerActionEvents(p2Raises, p3Calls);
		player2.assertReceivedPlayerActionEvents(p1Folds, p3Calls);
		player3.assertReceivedPlayerActionEvents(p1Folds, p2Raises);
	}

	public void testPlayersAreToldWhenTheyPayBlinds() throws Exception {
		player1.stack = 5;
		player2.stack = 5;
		player3.stack = 5;
		player1.actions.add(new Call(bigBlind));
		player2.actions.add(new Call(smallBlind));
		player3.actions.add(new Check());
		hand.play();
		player1.assertHasNotPaidBlind();
		player2.assertHasPaidBlind(smallBlind);
		player3.assertHasPaidBlind(bigBlind);
	}

	public void testPlayersAreToldTheWinningsAfterShowdown() throws Exception {
		player1.stack = 5;
		player2.stack = 5;
		player3.stack = 5;

		// dealer calls, paying 'bigBlind'
		player1.actions.add(new Call(bigBlind));

		// small blind folds, leaving 'smallBlind' to pot
		player2.actions.add(new Fold());

		// big blind checks, leaving 'bigBlind' to pot
		player3.actions.add(new Check());

		hand.play();
		Map<Player, Integer> distribution = new HashMap<Player, Integer>();
		distribution.put(player1, (2 * bigBlind) + smallBlind);
		player1.gotWinningNotification(distribution);
		player2.gotWinningNotification(distribution);
		player3.gotWinningNotification(distribution);
	}

	public void testReRaise() throws Exception {
		player1.stack = 10;
		player2.stack = 10;
		player3.stack = 10;

		int potSize = smallBlind + bigBlind;

		// dealer calls, paying 'bigBlind'
		player1.actions.add(new Call(bigBlind));
		potSize += bigBlind;

		// small blind calls 'smallBlind' and raises by 2
		player2.actions.add(new Raise(smallBlind, 2));
		potSize += (smallBlind + 2);

		// big blind calls the raise and reraises by 2
		player3.actions.add(new Raise(smallBlind, 2));
		potSize += (2 + 2);

		player1.actions.add(new Fold());
		player2.actions.add(new Fold());

		hand.play();
		Map<Player, Integer> distribution = new HashMap<Player, Integer>();
		distribution.put(player3, potSize);
		player1.gotWinningNotification(distribution);
		player2.gotWinningNotification(distribution);
		player3.gotWinningNotification(distribution);
	}

	public void testAllPlayersCheck() throws Exception {
		player1.stack = 10;
		player2.stack = 10;
		player3.stack = 10;
		player1.shouldAlwaysCheck = true;
		player2.shouldAlwaysCheck = true;
		player3.shouldAlwaysCheck = true;
		hand.play();
		assertEquals(1, player1.executedActions.size());
		assertEquals(1, player2.executedActions.size());
		assertEquals(1, player3.executedActions.size());
	}

	public void testAllPlayersCall() throws Exception {
		player1.stack = 10;
		player2.stack = 10;
		player3.stack = 10;
		player1.actions.add(new Call(0));
		player2.actions.add(new Call(0));
		player3.actions.add(new Call(0));
		hand.play();
		assertEquals(1, player1.executedActions.size());
		assertEquals(1, player2.executedActions.size());
		assertEquals(1, player3.executedActions.size());
	}

	/**
	 * Implementing HandListener
	 */
	public void onHandStart(List<Player> players) {
		actualEvents.add(new DummyEvent("hand starts", players.toArray()));
		for (Player p : players) {
			assertNotNull("Players should have cards when the 'hand starts' event is raised.", p.getCard());
		}
	}

	/**
	 * Implementing HandListener
	 */
	public void onBigBlind(BlindEvent e) {
		actualEvents.add(e);
	}

	/**
	 * Implementing HandListener
	 */
	public void onSmallBlind(BlindEvent e) {
		actualEvents.add(e);
	}

	/**
	 * Implementing HandListener
	 */
	public void onShowdown() {
		actualEvents.add(new DummyEvent("showdown"));
	}

	/**
	 * Implementing HandListener
	 */
	public void onHandEnd(Map<Player, Integer> wins) {
		actualEvents.add(new DummyEvent("hand ends"));
	}

	/**
	 * Implementing HandListener
	 */
	public void onPlayerAction(PlayerActionEvent e) {
		actualEvents.add(e);
	}

	/**
	 * Implementing HandListener
	 */
	public void onActionPrompt(PlayerPromptEvent e) {
		actualEvents.add(e);
	}

	/**
	 * Implementing HandListener
	 */
	public void onPlayerWin(PlayerWinEvent e) {
		actualEvents.add(e);
	}

	/**
	 * A custom event class for keeping track of expected events without a
	 * representative event object we could test against.
	 */
	private class DummyEvent extends HandEvent {

		private List<Object> identifiers;

		public DummyEvent(String name, Object... objects) {
			this.identifiers = new ArrayList<Object>();
			this.identifiers.add(name);
			this.identifiers.addAll(Arrays.asList(objects));
		}

		@Override
		public String toString() {
			return String.valueOf(identifiers);
		}

		@Override
		public boolean equals(Object obj) {
			return obj.getClass().equals(getClass()) && identifiers.equals(((DummyEvent) obj).identifiers);
		}
	}

}
