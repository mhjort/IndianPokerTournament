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

import java.util.List;
import java.util.Map;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;


/**
 * The <tt>Player</tt> interface is a server-side representation of a player.
 * In other words, a proxy that 1) communicates with the remote player bot, and
 * 2) decorates the player bot's functionality with player-specific information
 * that should be kept strictly server-side (e.g. the player's stack).
 *
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public interface Player {

	/**
	 * Invoked when a tournament is about to start.
	 *
	 * @param yourStack
	 *                The player's starting stack.
	 * @param seatingOrder
	 *                The seating order and players in the tournament.
	 */
	void tournamentBegins(int yourStack, List<Player> seatingOrder);

	/**
	 * Returns the player's card (the one she's holding against her
	 * forehead).
	 */
	Card getCard();

	/**
	 * Assigns a card to the player.
	 *
	 * @param card
	 *                The card that the player's supposedly holding against
	 *                her forehead.
	 */
	void setCard(Card card);

	/**
	 * Removes the indicated number of chips from the player's stack (moving
	 * them to the pot). This method is called when assigning forced blind
	 * bets.
	 *
	 * @param numberOfChips
	 *                The size of the blind.
	 */
	void blind(int numberOfChips);

	/**
	 * Removes the indicated number of chips from the player's stack (moving
	 * them to the pot). This method is called when a player bot has
	 * indicated the intent to bet N chips.
	 *
	 * @param numberOfChips
	 *                The number of chips to bet.
	 */
	void bet(int numberOfChips);

	/**
	 * Returns the name of the player bot. (e.g. "Bob", "Pocket Rockets",
	 * etc.)
	 */
	String getName();

	/**
	 * Invoked when a new hand is about to start.
	 *
	 * @param dealer
	 *                The <tt>Player</tt> on the "button" in the starting
	 *                hand.
	 * @param blinds
	 *                The players who are paying blinds in the starting
	 *                hand.
	 */
	void handStarts(Player dealer, Map<Player, Integer> blinds);

	/**
	 * Invoked when a hand ends.
	 *
	 * @param winnings
	 *                The pot distribution among winners.
	 */
	void handEnded(Map<Player, Integer> winnings);

	/**
	 * Invoked when another player has acted.
	 */
	void playerActed(PlayerActionEvent e);

	/**
	 * Invoked when a hand has ended.
	 */
	void showCard();

	/**
	 * Invoked when it's the player's action.
	 *
	 * @param priceToCall
	 *                The price to call, i.e. the difference between what
	 *                this player has bet and what someone else has bet.
	 * @param minimumRaise
	 *                The current minimum raise.
	 * @param potSize
	 *                The size of the pot before this player's next move.
	 * @return One of <tt>Fold</tt>, <tt>Check</tt> (already leveled
	 *         with the table and not betting more), <tt>Call</tt> (only
	 *         paying enough to stay in the hand), or <tt>Raise</tt>
	 *         (calling and betting on top). In case of the price to call
	 *         being higher than the player's stack, he can still choose to
	 *         <tt>Call</tt> with whatever he's got, effectively going
	 *         "all-in".
	 */
	Action action(int priceToCall, int minimumRaise, int potSize);

	/**
	 * Invoked when the player has won something from a hand.
	 *
	 * @param chips
	 *                The pot size (including your own bets!).
	 */
	void won(int chips);

	/**
	 * Indicates whether the player has any chips left in her stack.
	 */
	boolean hasChips();

	/**
	 * Returns the player's current remaining stack.
	 */
	int getStack();

	/**
	 * Returns the player's total bets in the current hand.
	 */
	int totalBets();
}
