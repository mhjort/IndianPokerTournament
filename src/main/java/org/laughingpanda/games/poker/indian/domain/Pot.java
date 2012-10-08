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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class Pot {
	int size = 0;

	private HandEventNotifier notifier;

	public Pot(HandEventNotifier notifier) {
		this.notifier = notifier;
	}

	public int getSize() {
		return size;
	}

	public void add(int amount) {
		size += amount;
	}

	public Map<Player, Integer> distribute(List<Player> showdownPlayers, List<Player> foldedPlayers) {
		Map<Player, Integer> foldedPlayersBets = betsAsMap(foldedPlayers);
		Map<Player, Integer> showdownPlayersBets = betsAsMap(showdownPlayers);

		showdownPlayers = new ArrayList<Player>(showdownPlayers);
		Map<Player, Integer> winnings = new HashMap<Player, Integer>();

		while (size > 0) {
			int stake = determineSmallestBetFrom(showdownPlayersBets);
			int sidePotSize = collectStakeFrom(showdownPlayersBets, stake);
			sidePotSize += collectStakeFrom(foldedPlayersBets, stake);

			SidePot sidePot = new SidePot();
			sidePot.addChips(sidePotSize);
			sidePot.setWinningPlayers(filterSidePotWinnersFrom(showdownPlayers));
			merge(winnings, sidePot.getDistribution());

			removePlayersWithMinimumOfChips(showdownPlayers);
			size -= sidePotSize;
		}
		handOut(winnings);
		// TODO: remove
		if (size > 0) {
			throw new RuntimeException("Not all chips were distributed! (" + size + ")");
		}
		return winnings;
	}

	private int determineSmallestBetFrom(Map<Player, Integer> playersBets) {
		ArrayList<Integer> bets = new ArrayList<Integer>(playersBets.values());
		Collections.sort(bets);
		for (Integer i : bets) {
			if (i > 0) {
				return i;
			}
		}
		throw new RuntimeException("ERROR: no players have any bets left to share");
	}

	private Map<Player, Integer> betsAsMap(List<Player> players) {
		Map<Player, Integer> bets = new HashMap<Player, Integer>();
		for (Player p : players) {
			bets.put(p, p.totalBets());
		}
		return bets;
	}

	private int collectStakeFrom(Map<Player, Integer> playersAndBets, int stake) {
		int pot = 0;
		for (Player p : playersAndBets.keySet()) {
			Integer remainingBets = playersAndBets.get(p);
			if (remainingBets == null) {
				remainingBets = 0;
			}
			int bet = Math.min(stake, remainingBets);
			playersAndBets.put(p, remainingBets - bet);
			pot += bet;
		}
		return pot;
	}

	private void handOut(Map<Player, Integer> winnings) {
		for (Player p : winnings.keySet()) {
			Integer chips = winnings.get(p);
			if (chips > 0) {
				p.won(chips);
				notifier.notifyListenersOfPlayerWinning(p, chips);
			}
		}
	}

	private void merge(Map<Player, Integer> existingWinnings, Map<Player, Integer> additionalWinnings) {
		for (Player p : additionalWinnings.keySet()) {
			Integer sum = existingWinnings.get(p);
			sum = additionalWinnings.get(p) + (sum != null ? sum : 0);
			existingWinnings.put(p, sum);
		}
	}

	private void removePlayersWithMinimumOfChips(List<Player> candidates) {
		int smallestBet = getSmallestBet(candidates);

		List<Player> remove = new ArrayList<Player>();
		for (Player p : candidates) {
			if (p.totalBets() == smallestBet) {
				remove.add(p);
			}
		}
		for (Player p : remove) {
			candidates.remove(p);
		}
	}

	private List<Player> filterSidePotWinnersFrom(List<Player> players) {
		players = sortWinningCardsFirst(players);
		Card winningCard = players.get(0).getCard();
		List<Player> winners = new ArrayList<Player>();
		for (Player p : players) {
			// TODO: implement "isEqualInRank" to the Card class
			if (p.getCard().compareTo(winningCard) >= 0) {
				winners.add(p);
			}
		}
		return winners;
	}

	private List<Player> sortWinningCardsFirst(List<Player> players) {
		return sort(players, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				return p2.getCard().compareTo(p1.getCard());
			}
		});
	}

	private int getSmallestBet(List<Player> candidates) {
		List<Player> sorted = sortSmallestBetsFirst(candidates);
		int smallestBet = sorted.get(0).totalBets();
		return smallestBet;
	}

	private List<Player> sortSmallestBetsFirst(List<Player> players) {
		return sort(players, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				return new Integer(p1.totalBets()).compareTo(p2.totalBets());
			}
		});
	}

	private List<Player> sort(List<Player> players, Comparator<Player> c) {
		List<Player> sorted = new ArrayList<Player>(players);
		Collections.sort(sorted, c);
		return sorted;
	}

}
