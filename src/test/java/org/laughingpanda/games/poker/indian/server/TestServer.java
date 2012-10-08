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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.Card;
import org.laughingpanda.games.poker.indian.domain.DefaultHandListener;
import org.laughingpanda.games.poker.indian.domain.DefaultTournamentListener;
import org.laughingpanda.games.poker.indian.domain.HandListener;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.PlayerProxy;
import org.laughingpanda.games.poker.indian.domain.TournamentListener;
import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestServer extends TestCase {

	public void testServerAcceptsPlayersIntoTheTournament() throws Exception {
		final CountDownLatch tournamentFinished = new CountDownLatch(1);
		Server server = new Server();
		server.registerUi(new ServerUi() {

			public List<HandListener> getHandListeners() {
				return Collections.singletonList((HandListener) new DefaultHandListener());
			}

			public List<TournamentListener> getTournamentListeners() {
				return Collections.singletonList((TournamentListener) new DefaultTournamentListener() {
					@Override
					public void onTournamentFinish(List<Player> results) {
						tournamentFinished.countDown();
					}
				});
			}

			public void start() {
			}
		});
		server.createTournament();
		MockPlayerProxy player1 = new MockPlayerProxy(new Fold());
		MockPlayerProxy player2 = new MockPlayerProxy(new Call(0));
		server.join(player1);
		server.join(player2);
		assertTrue(player1.isGetNameCalled);
		assertTrue(player2.isGetNameCalled);
		server.startTournament();
		assertTrue("Tournament didn't finish before timeout", tournamentFinished.await(5, TimeUnit.SECONDS));
	}

	private static class MockPlayerProxy implements PlayerProxy {

		boolean isGetNameCalled = false;

		private final Action action;

		public MockPlayerProxy(Action action) {
			this.action = action;
		}

		public Action action(int priceToCall, int minimumRaise, int potSize) {
			return action;
		}

		public void handStarts(Player dealer, Map<Player, Integer> blinds, Map<Player, Card> cards, Map<Player, Integer> stacks) {
		}

		public void paidBlind(int chips) {
		}

		public void performed(Action performedAction) {
		}

		public void playerActed(Player player, Action action) {
		}

		public void showCard(Card card) {
		}

		public void tournamentStarts(int stack, String[] playerNames) {
		}

		public String getName() {
			isGetNameCalled = true;
			return "Mock name";
		}

		public void won(int chips) {
		}
	}
}
