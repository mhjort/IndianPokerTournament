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
import java.util.List;

/**
 * A <tt>Tournament</tt> represents a single tournament consisting of a series
 * of <tt>Hand</tt>s. The number of <tt>Hand</tt>s played depends on how
 * long it takes for <tt>Player</tt>s to go broke and leave the table. Each
 * <tt>Hand</tt> consists of the first two players to the left from the dealer
 * placing forced bets (e.g. <i>blinds</i>), followed by a <i>betting round</i>
 * (ending to the <tt>Player</tt>s who paid the blinds).
 *
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class Tournament {

	private List<Player> players = new ArrayList<Player>();

	private List<Player> droppedPlayers = new ArrayList<Player>();

	private Player dealer;

	private int startingStack;

	private Deck deck;

	private TournamentDriver driver;

	private boolean started;

	private List<HandListener> handListeners = new ArrayList<HandListener>();

	private TournamentEventNotifier notifier = new TournamentEventNotifier();

	private BlindsStrategy blinds;

	public void join(Player player) {
		if (started) {
			throw new RuntimeException();
		}
		players.add(player);
		notifier.playerJoined(player);
	}

	public void start() {
		checkThatWeHaveEnoughPlayers();
		this.started = true;
		notifyPlayersOfTournamentStarting();
		driver.start(this);
	}

	private void checkThatWeHaveEnoughPlayers() {
		int minimumNumberOfPlayers = 2;
		if (players.size() < minimumNumberOfPlayers) {
			throw new IllegalStateException("At least " + minimumNumberOfPlayers + " players are required before starting a tournament (we have " + players.size() + ")");
		}
	}

	private void notifyPlayersOfTournamentStarting() {
		for (Player player : players) {
			player.tournamentBegins(startingStack, players);
		}
	}

	public List<Player> players() {
		return players;
	}

	public void setStartingStack(int stack) {
		startingStack = stack;
	}

	public void setBlinds(int smallBlind, int bigBlind) {
		setBlindsStrategy(new FixedBlinds(smallBlind, bigBlind));
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public boolean isDealer(Player player) {
		return player.equals(dealer);
	}

	public void setDriver(TournamentDriver driver) {
		this.driver = driver;
	}

	public void playNextHand() {
		assignDealer();
		shuffleCards();
		updateBlindLevels();
		Hand hand = initializeHand();
		hand.play();
		updateResultsList();
	}

	private void updateBlindLevels() {
		blinds.handIsOver();
	}

	private void updateResultsList() {
		List<Player> newDroppedPlayers = dropPlayersWithoutChips();
		Collections.sort(newDroppedPlayers, new Comparator<Player>() {

			public int compare(Player a, Player b) {
				return new Integer(a.totalBets()).compareTo(b.totalBets());
			}
		});
		for (Player p : newDroppedPlayers) {
			notifier.playerDropped(p);
		}
		droppedPlayers.addAll(newDroppedPlayers);
		if (players.size() == 1) {
			droppedPlayers.addAll(players);
		}
	}

	private List<Player> dropPlayersWithoutChips() {
		List<Player> newDroppedPlayers = new ArrayList<Player>();
		for (Player p : players) {
			if (!p.hasChips()) {
				newDroppedPlayers.add(p);
			}
		}
		for (Player p : newDroppedPlayers) {
			players.remove(p);
		}
		return newDroppedPlayers;
	}

	private Hand initializeHand() {
		Hand hand = new Hand();
		hand.setPlayers(players);
		hand.setDealer(dealer);
		hand.setBlinds(blinds.small(), blinds.big());
		hand.setDeck(deck);
		for (HandListener listener : handListeners) {
			hand.addListener(listener);
		}
		return hand;
	}

	private int dealerSeat() {
		return players.indexOf(dealer);
	}

	private void shuffleCards() {
		deck.shuffle();
	}

	private void assignDealer() {
		if (dealer == null) {
			dealer = players.get(0);
		} else {
			int dealerSeat = dealerSeat();
			dealer = playerOnSeat(dealerSeat + 1);
		}
	}

	private Player playerOnSeat(int seat) {
		return players.get(seat % players.size());
	}

	public boolean isFinished() {
		boolean isFinished = onlyOnePlayerHasChips();
		if (isFinished) {
			List<Player> results = new ArrayList<Player>(droppedPlayers);
			Collections.reverse(results);
			notifier.tournamentFinished(results);
		}
		return isFinished;
	}

	private boolean onlyOnePlayerHasChips() {
		int count = 0;
		for (Player p : players) {
			if (p.hasChips()) {
				count++;
			}
		}
		return (count == 1);
	}

	public void addHandListener(HandListener handListener) {
		handListeners.add(handListener);
	}

	public void addTournamentListener(TournamentListener listener) {
		notifier.addListener(listener);
	}

	public void setBlindsStrategy(BlindsStrategy strategy) {
		this.blinds = strategy;
	}
}
