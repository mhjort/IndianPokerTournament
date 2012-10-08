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
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestSidePot extends TestCase {

	private List<Player> players;

	private SidePot pot;

	@Override
	protected void setUp() throws Exception {
		players = new ArrayList<Player>();
		pot = new SidePot();
		pot.addChips(10);
	}

	public void testDistributingPotWhenPlayersHaveEqualBets() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(5, new Card(Suit.SPADES, 2));
		MockPlayer p2 = MockPlayer.createBettingPlayer(5, new Card(Suit.HEARTS, 2));

		players.add(p1);
		players.add(p2);
		pot.setWinningPlayers(players);
		Map<Player, Integer> winnings = pot.getDistribution();
		assertEquals(new Integer(5), winnings.get(p1));
		assertEquals(new Integer(5), winnings.get(p2));
	}

	public void testSidePotShouldOnlyDistributeTheLowestCommonStake() throws Exception {
		MockPlayer p1 = MockPlayer.createBettingPlayer(7, new Card(Suit.SPADES, 2));
		MockPlayer p2 = MockPlayer.createBettingPlayer(5, new Card(Suit.HEARTS, 2));

		players.add(p1);
		players.add(p2);
		pot.setWinningPlayers(players);
		Map<Player, Integer> winnings = pot.getDistribution();
		assertEquals(new Integer(5), winnings.get(p1));
		assertEquals(new Integer(5), winnings.get(p2));
	}
}
