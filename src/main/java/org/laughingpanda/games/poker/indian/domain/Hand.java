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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class Hand {

	private List<Player> players;

	private Pot pot;

	private Player dealer;

	private int smallBlind;

	private int bigBlind;

	private Deck deck;

	private HandEventNotifier notifier;

	private Player smallBlindPlayer;

	private Player bigBlindPlayer;

	public Hand() {
		notifier = new HandEventNotifier();
		// TODO: should this "player action" listener be registered from
		// outside Hand?
		notifier.register(new DefaultHandListener() {
			@Override
			public void onPlayerAction(PlayerActionEvent e) {
				for (Player p : players) {
					if (!p.equals(e.player)) {
						p.playerActed(e);
					}
				}
			}
		});
	}

	public void play() {
		pot = new Pot(notifier);
		dealCards();
		determineBlindPlayers();

		notifyPlayersOfHandStarting();
		notifier.notifyListenersOfHandStarting(players);
		Map<Player, Integer> bets = doBettingRound();
		notifier.notifyListenersOfShowdown();
		Map<Player, Integer> wins = distributeWins(bets);
		showCardsToPlayersInShowdown(bets);
		notifyPlayersOfHandEnding(wins);
		notifier.notifyListenersOfHandEnding(wins);
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public void setDealer(Player dealer) {
		this.dealer = dealer;
		determineBlindPlayers();
	}

	public void setBlinds(int smallBlind, int bigBlind) {
		this.smallBlind = smallBlind;
		this.bigBlind = bigBlind;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	private Map<Player, Integer> distributeWins(Map<Player, Integer> bets) {
		List<Player> showdownPlayers = new ArrayList<Player>();
		for (Player p : bets.keySet()) {
			if (p != null && bets.get(p) > 0) {
				showdownPlayers.add(p);
			}
		}

		ArrayList<Player> foldedPlayers = new ArrayList<Player>(players);
		foldedPlayers.removeAll(showdownPlayers);
		return pot.distribute(showdownPlayers, foldedPlayers);
	}

	// TODO This is copy-pasted from tournament
	private int dealerSeat() {
		return players.indexOf(dealer);
	}

	// TODO This is copy-pasted from tournament
	private Player playerOnSeat(int seat) {
		return players.get(seat % players.size());
	}

	private Map<Player, Integer> doBettingRound() {
		Set<Player> playersToAsk = new HashSet<Player>(this.players);

		Set<Player> folded = new HashSet<Player>();
		Map<Player, Integer> bets = newBettingRound();
		assignBlinds(bets);

		playersToAsk = removePlayersWithoutChips(playersToAsk);

		Player actingPlayer = playerAfterBigBlind(playersToAsk);
		int minimumRaise = bigBlind;
		int actions = 0;
		while (actingPlayer != null && !playersToAsk.isEmpty()) {
			if (allPlayersHaveHadChanceToAct(actions) && nonZeroBetsAreEven(bets)) {
				break;
			}

			int priceToCall = priceToCall(bets, actingPlayer);
			if (priceToCall == 0 && playersToAsk.size() == 1) {
				break;
			}

			int chipsPaid = 0;
			int potSize = potSize(bets, folded);
			notifier.notifyListenersOfPlayerPrompt(actingPlayer, priceToCall, minimumRaise, potSize);
			Action action = actingPlayer.action(priceToCall, minimumRaise, potSize);
			if (action instanceof Fold) {
				bets.remove(actingPlayer);
				folded.add(actingPlayer);
				playersToAsk.remove(actingPlayer);
			} else if (action instanceof Call || action instanceof Check) {
				chipsPaid = Math.min(priceToCall, actingPlayer.getStack());
				bet(bets, actingPlayer, chipsPaid);
			} else if (action instanceof Raise) {
				Raise raise = (Raise) action;
				chipsPaid = priceToCall + raise.amount();
				bet(bets, actingPlayer, chipsPaid);
				raise.setCallPrice(priceToCall);
			}
			notifier.notifyListenersOfPlayerAction(actingPlayer, action, chipsPaid);

			actions++;
			playersToAsk = removePlayersWithoutChips(playersToAsk);
			actingPlayer = nextPlayer(actingPlayer, playersToAsk);
		}
		return bets;
	}

	private Set<Player> removePlayersWithoutChips(Set<Player> playersToAsk) {
		Set<Player> remainingPlayers = new HashSet<Player>(playersToAsk);
		for (Player p : playersToAsk) {
			if (!p.hasChips()) {
				remainingPlayers.remove(p);
			}
		}
		return remainingPlayers;
	}

	private Player nextPlayer(Player actingPlayer, Collection<Player> playersToAsk) {
		Player next = null;
		int offset = 1;
		while (next != actingPlayer) {
			next = playerOnSeat(seatOfPlayer(actingPlayer) + offset);
			if (playersToAsk.contains(next)) {
				return next;
			}
			offset++;
		}
		return null;
	}

	private Player playerAfterBigBlind(Set<Player> playersToAsk) {
		return nextPlayer(bigBlindPlayer, playersToAsk);
	}

	private boolean allPlayersHaveHadChanceToAct(int actions) {
		return actions >= players.size();
	}

	private Map<Player, Integer> newBettingRound() {
		Map<Player, Integer> bets = new HashMap<Player, Integer>();
		for (Player player : players) {
			bets.put(player, 0);
		}
		return bets;
	}

	private boolean nonZeroBetsAreEven(Map<Player, Integer> bets) {
		int previous = 0;
		for (Player p : bets.keySet()) {
			int bet = bets.get(p);
			if (bet == 0) {
				continue;
			}
			if (previous != 0 && bet != previous) {
				return false;
			}
			previous = bet;
		}
		return true;
	}

	private int priceToCall(Map<Player, Integer> bets, Player player) {
		ArrayList<Integer> listOfBets = new ArrayList<Integer>(bets.values());
		Collections.sort(listOfBets);
		int highestBet = listOfBets.get(listOfBets.size() - 1);
		return highestBet - bets.get(player);
	}

	private int potSize(Map<Player, Integer> bets, Collection<Player> foldedPlayers) {
		int pot = 0;
		for (int bet : bets.values()) {
			pot += bet;
		}
		for (Player foldedPlayer : foldedPlayers) {
			pot += foldedPlayer.totalBets();
		}
		return pot;
	}

	private void assignBlinds(Map<Player, Integer> bets) {
		int adjustedSmallBlind = Math.min(smallBlind, smallBlindPlayer.getStack());
		int adjustedBigBlind = Math.min(bigBlind, bigBlindPlayer.getStack());
		betSmallBlind(bets, adjustedSmallBlind);
		betBigBlind(bets, adjustedBigBlind);
		notifier.notifyListenersOfSmallBlind(smallBlindPlayer, adjustedSmallBlind);
		notifier.notifyListenersOfBigBlind(bigBlindPlayer, adjustedBigBlind);
	}

	private void determineBlindPlayers() {
		smallBlindPlayer = playerOnSeat(dealerSeat() + 1);
		bigBlindPlayer = playerOnSeat(dealerSeat() + 2);
	}

	private void betSmallBlind(Map<Player, Integer> bets, int blind) {
		smallBlindPlayer.blind(blind);
		bet(bets, smallBlindPlayer, blind);
	}

	private void betBigBlind(Map<Player, Integer> bets, int blind) {
		bigBlindPlayer.blind(blind);
		bet(bets, bigBlindPlayer, blind);
	}

	private void bet(Map<Player, Integer> bets, Player player, int sizeOfBet) {
		player.bet(sizeOfBet);
		adjustChipsAfterBet(player, bets, sizeOfBet);
	}

	private void adjustChipsAfterBet(Player player, Map<Player, Integer> bets, int chips) {
		pot.add(chips);
		int oldBet = bets.get(player);
		bets.put(player, oldBet + chips);
	}

	private int seatOfPlayer(Player player) {
		return players.indexOf(player);
	}

	private void dealCards() {
		for (Player player : players) {
			player.setCard(deck.nextCard());
		}
	}

	private void showCardsToPlayersInShowdown(Map<Player, Integer> bets) {
		for (Player player : players) {
			if (bets.containsKey(player) && bets.get(player) > 0) {
				player.showCard();
			}
		}
	}

	private void notifyPlayersOfHandStarting() {
		Map<Player, Integer> blinds = new HashMap<Player, Integer>();
		blinds.put(smallBlindPlayer, smallBlind);
		blinds.put(bigBlindPlayer, bigBlind);
		for (Player player : players) {
			player.handStarts(dealer, blinds);
		}
	}

	private void notifyPlayersOfHandEnding(Map<Player, Integer> winnings) {
		for (Player player : players) {
			player.handEnded(winnings);
		}
	}

	public void addListener(HandListener listener) {
		notifier.register(listener);
	}
}
