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

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TournamentEventNotifier {
	private List<TournamentListener> tournamentListeners = new ArrayList<TournamentListener>();

	public void addListener(TournamentListener listener) {
		tournamentListeners.add(listener);
	}

	public void playerJoined(Player player) {
		for (TournamentListener listener : tournamentListeners) {
			listener.onPlayerJoin(player);
		}
	}

	public void playerDropped(Player player) {
		for (TournamentListener listener : tournamentListeners) {
			listener.onPlayerDrop(player);
		}
	}

	public void tournamentFinished(List<Player> results) {
		for (TournamentListener listener : tournamentListeners) {
			listener.onTournamentFinish(results);
		}
	}
}
