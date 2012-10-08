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
import java.util.Collections;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestHands extends AbstractTournamentTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tournament.join(player1);
		tournament.join(player2);
		tournament.join(player3);
	}

	public void testFirstPlayerStartsAsTheDealer() throws Exception {
		tournament.start();
		driver.kickOffNextHand();
		dealerShouldBe(player1);
	}

	public void testFirstAndSecondPlayersAfterDealerPayBlinds() throws Exception {
		tournament.start();
		driver.kickOffNextHand();
		assertEquals(Collections.singletonList(new Fold()), player1.executedActions);
		assertEquals(Collections.singletonList(new Fold()), player2.executedActions);
		assertEquals(new ArrayList<Action>(), player3.executedActions);
		player1.hasBet(0);
		player2.hasBet(smallBlind);
		player3.hasBet(bigBlind);
		assertEquals(0, player1.getChipsWon());
		assertEquals(0, player2.getChipsWon());
		assertEquals(smallBlind + bigBlind, player3.getChipsWon());

		player1.hasStack(1000);
		player2.hasStack(1000 - smallBlind);
		player3.hasStack(1000 + smallBlind);
	}

	public void testDealerAndBlindsRotateEveryHand() throws Exception {
		tournament.start();

		// first hand
		tellAllPlayersToFoldTheirHand();
		driver.kickOffNextHand();
		dealerShouldBe(player1);
		player1.hasBet(0);
		player2.hasBet(smallBlind);
		player3.hasBet(bigBlind);

		// second hand
		tellAllPlayersToFoldTheirHand();
		driver.kickOffNextHand();
		dealerShouldBe(player2);
		player1.hasBet(bigBlind);
		player2.hasBet(0);
		player3.hasBet(smallBlind);

		// third hand (everyone should be even with blinds)
		tellAllPlayersToFoldTheirHand();
		driver.kickOffNextHand();
		dealerShouldBe(player3);
		player1.hasBet(smallBlind);
		player2.hasBet(bigBlind);
		player3.hasBet(0);

		// fourth hand (dealer should rotate back to 1st position)
		tellAllPlayersToFoldTheirHand();
		driver.kickOffNextHand();
		dealerShouldBe(player1);
		player1.hasBet(0);
		player2.hasBet(smallBlind);
		player3.hasBet(bigBlind);
	}

	/**
	 * Configures all mock players to fold their hand when asked for action.
	 * This helps in keeping track of blinds being paid correctly.
	 */
	private void tellAllPlayersToFoldTheirHand() {
		for (MockPlayer p : players) {
			p.actions.add(new Fold());
		}
	}

	public void testDeckIsShuffledBetweenHands() throws Exception {
		int initialShuffles = deck.numberOfShuffles;
		tournament.start();
		assertEquals(initialShuffles, deck.numberOfShuffles);
		driver.kickOffNextHand();
		assertEquals(initialShuffles + 1, deck.numberOfShuffles);
		driver.kickOffNextHand();
		assertEquals(initialShuffles + 2, deck.numberOfShuffles);
	}

	private void dealerShouldBe(Player player) {
		assertTrue("Expected player " + player.getName() + " to be the dealer.", tournament.isDealer(player));
		for (Player nonDealer : players) {
			if (nonDealer != player) {
				assertFalse("There should only ever be exactly one dealer.", tournament.isDealer(nonDealer));
			}
		}
	}

}
