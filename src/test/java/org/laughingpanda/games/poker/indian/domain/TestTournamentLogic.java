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

import java.util.Arrays;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestTournamentLogic extends AbstractTournamentTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tournament.join(player1);
		tournament.join(player2);
		tournament.join(player3);
	}

	public void testTournamentRegistersItselfWithTheTournamentDriverUponStart() throws Exception {
		driver.hasNotBeenStarted();
		tournament.start();
		driver.wasStartedBy(tournament);
	}

	public void testPlayersAreToldTheirStackWhenTournamentStarts() throws Exception {
		tournament.start();
		playersHaveStack(startingStack, player1, player2, player3);
		assertPlayersGotSeatingOrder(player1, player2, player3);
		assertPlayersDontHaveCards();
	}

	public void testPlayersAreDealtCardsWhenHandStarts() throws Exception {
		DefaultDeck deck = new DefaultDeck();
		deck.shuffle();
		tournament.setDeck(deck);
		tournament.start();
		assertPlayersDontHaveCards();
		driver.kickOffNextHand();
		assertPlayersHaveCards();
	}

	private void assertPlayersHaveCards() {
		for (Player player : tournament.players()) {
			assertNotNull(player.getCard());
		}
	}

	private void assertPlayersGotSeatingOrder(MockPlayer... seatingOrder) {
		for (MockPlayer player : seatingOrder) {
			assertTrue(player.players.equals(Arrays.asList(seatingOrder)));
		}
	}

	private void assertPlayersDontHaveCards() {
		for (Player player : tournament.players()) {
			MockPlayer mockPlayer = (MockPlayer) player;
			assertNull(mockPlayer.getCard());
		}
	}

}
