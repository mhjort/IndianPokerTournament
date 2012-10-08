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


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestTournamentRegistration extends AbstractTournamentTestCase {

	public void testPlayersCanJoinTournamentUntilItsStarted() throws Exception {
		joinThreePlayersToTournament();
		tournament.start();
		try {
			tournament.join(player1);
			fail("Should not be able to join a tournament once it's started");
		} catch (Exception expected) {
		}
	}

	public void testGameCannotBeStartedWithoutAtLeastTwoPlayers()
			throws Exception {
		assertCannotStartTournament("Tournament must not be startable with no players.");
		tournament.join(player1);
		assertCannotStartTournament("Tournament must not be startable with just one player.");
		tournament.join(player2);
		tournament.start();
	}

	private void assertCannotStartTournament(String message) {
		try {
			tournament.start();
			fail(message);
		} catch (Exception expected) {
		}
	}

	public void testServerKnowsWhoIsInTheTournament() throws Exception {
		tournament.join(player1);
		tournament.join(player2);
		assertPlayerIsInTheTournament(player1);
		assertPlayerIsInTheTournament(player2);
		assertPlayerIsNotInTheTournament(player3);
		tournament.start();
		assertPlayerIsInTheTournament(player1);
		assertPlayerIsInTheTournament(player2);
		assertPlayerIsNotInTheTournament(player3);
	}

	private void joinThreePlayersToTournament() {
		tournament.join(player1);
		tournament.join(player2);
		tournament.join(player3);
	}

	private void assertPlayerIsInTheTournament(Player player) {
		assertTrue(tournament.players().contains(player));
	}

	private void assertPlayerIsNotInTheTournament(Player player) {
		assertFalse(tournament.players().contains(player));
	}
}
