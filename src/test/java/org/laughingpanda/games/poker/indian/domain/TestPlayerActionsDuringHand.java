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

import java.util.HashMap;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestPlayerActionsDuringHand extends TestCase {

	private Tournament tournament;

	private MockDriver driver;

	private MockPlayer player1, player2, player3, player4;

	private FixedDeck deck;

	private int startingStack, minimumRaise, smallBlind, bigBlind;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		driver = new MockDriver();
		prepareEssentialNumbers();
		prepareDeck();
		preparePlayers();
		prepareTournament();

		startHand();
	}

	private void prepareEssentialNumbers() {
		smallBlind = 5;
		bigBlind = 10;
		minimumRaise = bigBlind;
		startingStack = 100;
	}

	private void preparePlayers() {
		player1 = new MockPlayer("P1");
		player2 = new MockPlayer("P2");
		player3 = new MockPlayer("P3");
		player4 = new MockPlayer("P4");
	}

	private void prepareDeck() {
		deck = new FixedDeck();
		deck.add(new Card(Suit.CLUBS, 9)); // p1 will get a nine
		deck.add(new Card(Suit.CLUBS, 1)); // p2 will get an ace
		deck.add(new Card(Suit.SPADES, 13)); // p3 will get a king
		deck.add(new Card(Suit.HEARTS, 2)); // p4 will get a deuce
	}

	private void prepareTournament() {
		tournament = new Tournament();
		tournament.setDriver(driver);
		tournament.setDeck(deck);
		tournament.setBlinds(smallBlind, bigBlind);
		tournament.setStartingStack(startingStack);
		tournament.join(player1);
		tournament.join(player2);
		tournament.join(player3);
		tournament.join(player4);
	}

	public void testHighestCardWinsInShowdownUsingMockPlayers() throws Exception {

		// player4 begins by folding with his deuce
		player4.actions.add(new Fold());

		// player1 thinks he's got a winner with a niner
		player1.actions.add(new Call(bigBlind));

		// player2 obviously calls from a blind position with an ace
		player2.actions.add(new Call(smallBlind));

		// player3 also obviously calls with a king
		player3.actions.add(new Check());

		// this.player1 = player1;
		// this.player2 = player2;
		// this.player3 = player3;
		// this.player4 = player4;
		tournament.start();
		driver.kickOffNextHand();

		assertEquals(bigBlind, player1.totalBets());
		assertEquals(bigBlind, player2.totalBets());
		assertEquals(bigBlind, player3.totalBets());
		assertEquals(0, player4.totalBets());
		assertEquals((2 * smallBlind) + (2 * bigBlind), player2.getChipsWon());
	}

	public void testPlayingAHandWherePlayersFoldRaiseAndCall() throws Exception {

		// action/betting
		player4.actions.add(new Fold());

		// player1 calls, i.e. pays the big blind
		player1.actions.add(new Call(bigBlind));

		// player2 raises from a blind position with an ace
		player2.actions.add(new Raise(smallBlind, minimumRaise));

		// player3 calls the raise with a king
		player3.actions.add(new Call(minimumRaise));

		// (player4 has already folded)

		// player1 folds, realizing that the others have high cards
		player1.actions.add(new Fold());

		tournament.start();
		driver.kickOffNextHand();

		// two players have bet a total of big blind + player2's raise
		assertEquals(bigBlind, player1.totalBets());
		assertEquals(bigBlind + minimumRaise, player2.totalBets());
		assertEquals(bigBlind + minimumRaise, player3.totalBets());
		assertEquals(0, player4.totalBets());

		assertEquals((3 * bigBlind) + (2 * minimumRaise), player2.getChipsWon());
		assertEquals(0, player1.getChipsWon());
		assertEquals(0, player3.getChipsWon());
		assertEquals(0, player4.getChipsWon());
	}

	public void testTheOnlyPlayerWhichDoesNotFoldAllTheTimeWinsTheTournament() throws Exception {
		MockPlayer playerWhoFolds = new MockPlayer("P1") {
			@Override
			public Action action(int priceToCall, int minimumRaise, int potSize) {
				return new Fold();
			}
		};
		MockPlayer playerWhoCalls = new MockPlayer("P2") {
			@Override
			public Action action(int priceToCall, int minimumRaise, int potSize) {
				return new Call(priceToCall);
			}
		};

		tournament = new Tournament();
		tournament.setStartingStack(3);
		tournament.setBlinds(1, 2);
		tournament.join(playerWhoFolds);
		tournament.join(playerWhoCalls);
		tournament.setDeck(deck);
		tournament.setDriver(driver);

		tournament.start();
		assertFalse(tournament.isFinished());

		driver.kickOffNextHand();
		assertFalse(tournament.isFinished());

		driver.kickOffNextHand();
		assertTrue(tournament.isFinished());

		playerWhoCalls.hasStack(3 * 2);
		playerWhoFolds.hasStack(0);
	}

	private void startHand() {
		player1.handStarts(player1, new HashMap<Player, Integer>());
		player2.handStarts(player1, new HashMap<Player, Integer>());
		player3.handStarts(player1, new HashMap<Player, Integer>());
		player4.handStarts(player1, new HashMap<Player, Integer>());

		player1.setCard(deck.card(0));
		player2.setCard(deck.card(1));
		player3.setCard(deck.card(2));
		player4.setCard(deck.card(3));
	}

}
