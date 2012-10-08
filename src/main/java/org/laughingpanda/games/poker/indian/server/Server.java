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

import org.laughingpanda.games.poker.indian.domain.HandListener;
import org.laughingpanda.games.poker.indian.domain.PlayerDecorator;
import org.laughingpanda.games.poker.indian.domain.PlayerProxy;
import org.laughingpanda.games.poker.indian.domain.RealTournamentDriver;
import org.laughingpanda.games.poker.indian.domain.Tournament;
import org.laughingpanda.games.poker.indian.domain.TournamentListener;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class Server {

	private ServerUi ui;

	private Tournament tournament;

	public Tournament createTournament() {
		tournament = new Tournament();
		registerUiListeners(tournament);
		return tournament;
	}

	public void registerUi(ServerUi ui) {
		this.ui = ui;
	}

	private void registerUiListeners(Tournament tournament) {
		for (HandListener listener : ui.getHandListeners()) {
			tournament.addHandListener(listener);
		}
		for (TournamentListener listener : ui.getTournamentListeners()) {
			tournament.addTournamentListener(listener);
		}
	}

	public void startTournament() {
		tournament.setBlindsStrategy(Configuration.getBlindsStrategy());
		tournament.setStartingStack(Configuration.getStartingStack());
		tournament.setDeck(Configuration.getDeck());
		tournament.setDriver(new RealTournamentDriver());
		tournament.start();
	}

	public void join(PlayerProxy proxy) {
		tournament.join(new PlayerDecorator(proxy.getName(), proxy));
	}
}
