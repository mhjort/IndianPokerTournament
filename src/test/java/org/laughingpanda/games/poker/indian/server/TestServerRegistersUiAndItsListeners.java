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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.DefaultDeck;
import org.laughingpanda.games.poker.indian.domain.DefaultHandListener;
import org.laughingpanda.games.poker.indian.domain.HandListener;
import org.laughingpanda.games.poker.indian.domain.MockPlayer;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.Tournament;
import org.laughingpanda.games.poker.indian.domain.TournamentListener;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestServerRegistersUiAndItsListeners extends TestCase {

	private Server server;

	private MockServerUi ui;

	private MockPlayer mockPlayer;

	@Override
	protected void setUp() throws Exception {
		server = new Server();
		ui = new MockServerUi();
		server.registerUi(ui);
		playHand();
	}

	public void testServerUiHandListenerShouldBeCalled() throws Exception {
		assertTrue("Server UI should get hand started event.", ui.onHandStartCalled);
	}

	public void testServerUiTournamentListenerShouldBeCalled() throws Exception {
		assertSame("Server UI should get player joined event.", mockPlayer, ui.playerJoined);
	}

	private void playHand() {
		Tournament tournament = server.createTournament();
		mockPlayer = new MockPlayer();
		tournament.setStartingStack(10);
		tournament.setBlinds(0, 0);
		tournament.join(mockPlayer);
		tournament.setDeck(new DefaultDeck());
		tournament.playNextHand();
	}

	private static class MockServerUi extends DefaultHandListener implements ServerUi, TournamentListener {

		Player playerJoined;

		boolean onHandStartCalled;

		@Override
		public void onHandStart(List<Player> players) {
			onHandStartCalled = true;
		}

		public List<HandListener> getHandListeners() {
			ArrayList<HandListener> list = new ArrayList<HandListener>();
			list.add(this);
			return list;
		}

		public void onPlayerJoin(Player player) {
			playerJoined = player;
		}

		public void onPlayerDrop(Player player) {
		}

		public void onTournamentFinish(List<Player> results) {
			throw new UnsupportedOperationException("Method not implemented.");
		}

		public List<TournamentListener> getTournamentListeners() {
			ArrayList<TournamentListener> list = new ArrayList<TournamentListener>();
			list.add(this);
			return list;
		}

		public void start() {
			throw new UnsupportedOperationException("Method not implemented.");
		}
	}
}
