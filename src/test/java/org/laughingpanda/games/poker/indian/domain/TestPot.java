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

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestPot extends TestCase {

	private Pot pot;

	@Override
	protected void setUp() throws Exception {
		pot = new Pot(new HandEventNotifier());
	}

	public void testShouldBeZeroWithNewPot() throws Exception {
		assertEquals(0, pot.getSize());
	}

	public void testShouldWorkWithOneAddition() throws Exception {
		pot.add(3);
		assertEquals(3, pot.getSize());
	}

	public void testShouldWorkWithMultipleAdditions() throws Exception {
		pot.add(3);
		pot.add(2);
		assertEquals(5, pot.getSize());
	}

	public void testWinnerGetsTheWholePot() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 8));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 7));
		pot.add(12);
		pot.distribute(getAsList(p1, p2, p3), new ArrayList<Player>());
		assertEquals(12, p1.getChipsWon());
		assertEquals(0, p2.getChipsWon());
		assertEquals(0, p3.getChipsWon());
	}

	public void testTheWholePotIsSharedWithAllPlayers() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(4, new Card(Suit.CLUBS, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 9));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 9));
		pot.add(12);
		pot.distribute(getAsList(p1, p2, p3), new ArrayList<Player>());
		assertEquals(4, p1.getChipsWon());
		assertEquals(4, p2.getChipsWon());
		assertEquals(4, p3.getChipsWon());
	}

	public void testPotIsSharedUnEvenlyWithAllPlayers() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(4, new Card(Suit.CLUBS, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 9));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 9));
		MockPlayer p4 = MockPlayer.createBettingPlayer(2, new Card(Suit.HEARTS, 2));
		pot.add(14);
		pot.distribute(getAsList(p1, p2, p3), getAsList(p4));
		boolean p1GotShortStraw = p1.getChipsWon() == 4 && p2.getChipsWon() == 5 && p3.getChipsWon() == 5;
		boolean p2GotShortStraw = p1.getChipsWon() == 5 && p2.getChipsWon() == 4 && p3.getChipsWon() == 5;
		boolean p3GotShortStraw = p1.getChipsWon() == 5 && p2.getChipsWon() == 5 && p3.getChipsWon() == 4;
		assertTrue("One player should've drawn the short straw (" + p1.getChipsWon() + ", " + p2.getChipsWon() + ", " + p3.getChipsWon() + ")", p1GotShortStraw || p2GotShortStraw || p3GotShortStraw);
	}

	public void testWinnerGetsTheMainPotAndRunnerUpGetsTheSidePot() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(2, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 8));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 7));
		pot.add(10);
		pot.distribute(getAsList(p1, p2, p3), new ArrayList<Player>());
		assertEquals(6, p1.getChipsWon());
		assertEquals(4, p2.getChipsWon());
		assertEquals(0, p3.getChipsWon());
	}

	public void testWinnerGetsTheMainPotAndRunnerUpsShareTheSidePot() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(2, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 8));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 8));
		pot.add(10);
		pot.distribute(getAsList(p1, p2, p3), new ArrayList<Player>());
		assertEquals(2 + 2 + 2, p1.getChipsWon());
		assertEquals(2, p2.getChipsWon());
		assertEquals(2, p3.getChipsWon());
	}

	public void testTwoWinnersGetsTheMainPotAndRunnerUpGetsTheSidePot() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(2, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(2, new Card(Suit.CLUBS, 9));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 8));
		pot.add(8);
		pot.distribute(getAsList(p1, p2, p3), new ArrayList<Player>());
		assertEquals(3, p1.getChipsWon());
		assertEquals(3, p2.getChipsWon());
		assertEquals(2, p3.getChipsWon());
	}

	public void testTwoWinnersGetsTheMainPotAndOneOfThemAlsoGetsTheSidePot() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(2, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.CLUBS, 9));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 8));
		pot.add(10);
		pot.distribute(getAsList(p1, p2, p3), new ArrayList<Player>());
		assertEquals(0, p3.getChipsWon());
		assertEquals(3, p1.getChipsWon());
		assertEquals(3 + 4, p2.getChipsWon());
	}

	public void testFoldedPlayerHasLessBetsThanWinner() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 8));
		MockPlayer p3 = MockPlayer.createBettingPlayer(2, new Card(Suit.HEARTS, 2));
		pot.add(10);
		pot.distribute(getAsList(p1, p2), getAsList(p3));
		assertEquals(0, p3.getChipsWon());
		assertEquals(10, p1.getChipsWon());
		assertEquals(0, p2.getChipsWon());
	}

	public void testFoldedPlayerHasLessBetsThanSharedWinners() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 9));
		MockPlayer p3 = MockPlayer.createBettingPlayer(2, new Card(Suit.HEARTS, 2));
		pot.add(10);
		pot.distribute(getAsList(p1, p2), getAsList(p3));
		assertEquals(0, p3.getChipsWon());
		assertEquals(5, p1.getChipsWon());
		assertEquals(5, p2.getChipsWon());
	}

	public void testFoldedPlayerHasMoreBetsThanWinner() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(2, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(6, new Card(Suit.HEARTS, 8));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 2));
		pot.add(12);
		pot.distribute(getAsList(p1, p2), getAsList(p3));
		assertEquals(0, p3.getChipsWon());
		assertEquals(6, p1.getChipsWon());
		assertEquals(6, p2.getChipsWon());
	}

	public void testPotReturnsTheWinningsForEachPlayer() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(2, new Card(Suit.SPADES, 9));
		MockPlayer p2 = MockPlayer.createBettingPlayer(4, new Card(Suit.SPADES, 8));
		MockPlayer p3 = MockPlayer.createBettingPlayer(4, new Card(Suit.HEARTS, 7));
		pot.add(10);
		Map<Player, Integer> expected = new HashMap<Player, Integer>();
		expected.put(p1, 6);
		expected.put(p2, 4);
		assertEquals(expected, pot.distribute(getAsList(p1, p2, p3), new ArrayList<Player>()));
	}

	private List<Player> getAsList(Player... players) {
		return new ArrayList<Player>(Arrays.asList(players));
	}
}
