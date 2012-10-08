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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class SidePot {

	private List<Player> players;

	private int size;

	public void setWinningPlayers(List<Player> players) {
		this.players = players;
	}

	public void addChips(int chips) {
		this.size += chips;
	}

	public Map<Player, Integer> getDistribution() {
		Map<Player, Integer> winnings = new HashMap<Player, Integer>();
		int unEvenChips = size % this.players.size();
		List<Player> evenPartPlayers = new ArrayList<Player>();
		List<Player> extraWinningPlayers = new ArrayList<Player>();
		divideEvenPartPlayersAndExtraWinningPlayers(unEvenChips, this.players, evenPartPlayers, extraWinningPlayers);
		distributeForEveryPlayer(winnings, evenPartPlayers, getEvenWinningShare(this.players));
		distributeForEveryPlayer(winnings, extraWinningPlayers, getEvenWinningShare(this.players) + 1);
		return winnings;
	}

	private void divideEvenPartPlayersAndExtraWinningPlayers(int unEvenChips, List<Player> players, List<Player> evenPartPlayers, List<Player> extraWinningPlayers) {
		evenPartPlayers.addAll(players);
		for (int i = 0; i < unEvenChips; i++) {
			Player extraWinningPlayer = evenPartPlayers.get(new Random().nextInt(evenPartPlayers.size()));
			evenPartPlayers.remove(extraWinningPlayer);
			extraWinningPlayers.add(extraWinningPlayer);
		}
	}

	private void distributeForEveryPlayer(Map<Player, Integer> winnings, List<Player> players, int chips) {
		for (Player player : players) {
			winnings.put(player, chips);
		}
	}

	private int getEvenWinningShare(List<Player> players) {
		return size / players.size();
	}
}