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
public class TestPotWithTwoSidePots extends TestCase {

	private Pot pot;

	private Map<MockPlayer, Integer> expectedWinnings;

	private MockPlayer p1;

	private MockPlayer p2;

	private MockPlayer p3;

	private MockPlayer p4;

	@Override
	protected void setUp() throws Exception {
		pot = new Pot(new HandEventNotifier());
		expectedWinnings = new HashMap<MockPlayer, Integer>();
	}

	public void testOneWinsAll() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 2), 0);
		p2 = createPlayer(4, new Card(Suit.SPADES, 3), 0);
		p3 = createPlayer(8, new Card(Suit.SPADES, 4), 0);
		p4 = createPlayer(8, new Card(Suit.SPADES, 5), 22);
		verifyPotDistribution();
	}

	public void testTwoSplitAll() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 2), 0);
		p2 = createPlayer(4, new Card(Suit.SPADES, 3), 0);
		p3 = createPlayer(8, new Card(Suit.SPADES, 5), 11);
		p4 = createPlayer(8, new Card(Suit.HEARTS, 5), 11);
		verifyPotDistribution();
	}

	public void testOnePlayerWinsMainPotAndOneWinsOneSidePot() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 2), 0);
		p2 = createPlayer(4, new Card(Suit.SPADES, 6), 14);
		p3 = createPlayer(8, new Card(Suit.SPADES, 3), 0);
		p4 = createPlayer(8, new Card(Suit.HEARTS, 5), 8);
		verifyPotDistribution();
	}

	public void testOnePlayerWinsMainPotAndTwoShareOneSidePot() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 2), 0);
		p2 = createPlayer(4, new Card(Suit.SPADES, 6), 14);
		p3 = createPlayer(8, new Card(Suit.SPADES, 5), 4);
		p4 = createPlayer(8, new Card(Suit.HEARTS, 5), 4);
		verifyPotDistribution();
	}

	public void testAllSidePotsGoToDifferentWinners() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 5), 8);
		p2 = createPlayer(4, new Card(Suit.SPADES, 4), 6);
		p3 = createPlayer(8, new Card(Suit.SPADES, 3), 8);
		p4 = createPlayer(8, new Card(Suit.SPADES, 2), 0);
		verifyPotDistribution();
	}

	public void testTwoPotsGoToDifferentWinnersAndLastSidePotIsSplit() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 5), 8);
		p2 = createPlayer(4, new Card(Suit.SPADES, 4), 6);
		p3 = createPlayer(8, new Card(Suit.SPADES, 3), 4);
		p4 = createPlayer(8, new Card(Suit.HEARTS, 3), 4);
		verifyPotDistribution();
	}

	public void testFirstPotIsSplitAndRestGoToDifferentWinners() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 5), 4);
		p2 = createPlayer(4, new Card(Suit.HEARTS, 5), 10);
		p3 = createPlayer(8, new Card(Suit.SPADES, 3), 8);
		p4 = createPlayer(8, new Card(Suit.HEARTS, 2), 0);
		verifyPotDistribution();
	}

	public void testFirstAndLastPotAreWonButSecondPotIsSplit() throws Exception {
		p1 = createPlayer(2, new Card(Suit.SPADES, 5), 8);
		p2 = createPlayer(4, new Card(Suit.HEARTS, 3), 3);
		p3 = createPlayer(8, new Card(Suit.SPADES, 3), 11);
		p4 = createPlayer(8, new Card(Suit.HEARTS, 2), 0);
		verifyPotDistribution();
	}

	private List<Player> getAsList(Player... players) {
		return new ArrayList<Player>(Arrays.asList(players));
	}

	private void verifyPotDistribution() {
		pot.distribute(getAsList(p1, p2, p3, p4), getAsList());
		StringBuffer s = new StringBuffer("Invalid pot distribution:\n");
		boolean success = true;
		for (MockPlayer p : expectedWinnings.keySet()) {
			Integer expected = expectedWinnings.get(p);
			if (expected.equals(new Integer(p.getChipsWon()))) {
				s.append(p.getName() + " got ");
				s.append(expected);
				s.append(" chips as expected.\n");
			} else {
				s.append(p.getName() + " got ");
				s.append(p.getChipsWon());
				s.append(" chips instead of the expected ");
				s.append(expected).append(".\n");
				success = false;
			}
		}
		assertTrue(s.toString(), success);
	}

	private MockPlayer createPlayer(int bet, Card card, int chipsToWin) {
		MockPlayer player = MockPlayer.createBettingPlayer(bet, card);
		player.setName(card.toString());
		expectedWinnings.put(player, chipsToWin);
		pot.add(bet);
		return player;
	}

}
