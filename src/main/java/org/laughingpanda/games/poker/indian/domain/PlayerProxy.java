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

import java.util.Map;

import org.laughingpanda.games.poker.indian.domain.actions.Action;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public interface PlayerProxy {

	/**
	 * Notification of the tournament starting.
	 *
	 * @param stack
	 *                The starting stack for all players.
	 * @param playerNames
	 *                The names of all players, indicating the seating
	 *                order.
	 */
	void tournamentStarts(int stack, String[] playerNames);

	/**
	 * Notification of a new hand starting.
	 *
	 * @param dealer
	 *                The dealer.
	 * @param blinds
	 *                The blinds and their size in chips for the starting
	 *                hand.
	 * @param cards
	 *                The cards of all players.
	 * @param stacks
	 *                The stacks of all players.
	 */
	void handStarts(Player dealer, Map<Player, Integer> blinds, Map<Player, Card> cards, Map<Player, Integer> stacks);

	/**
	 * A notification of another player's action.
	 *
	 * @param player
	 *                The other player.
	 * @param action
	 *                The other player's action.
	 */
	void playerActed(Player player, Action action);

	/**
	 * Prompts the player for her next action.
	 *
	 * @param priceToCall
	 *                The chips required for a "call"
	 * @param minimumRaise
	 *                The minimum raise (on top of <tt>priceToCall</tt>)
	 * @param potSize
	 *                The size of the pot before the player's action.
	 * @return The player's action.
	 */
	Action action(int priceToCall, int minimumRaise, int potSize);

	/**
	 * Informs the player her card in showdown (if the player is in the
	 * showdown).
	 */
	void showCard(Card card);

	/**
	 * Informs the player of paying a blind bet.
	 */
	void paidBlind(int chips);

	/**
	 * Indicates the action performed based on the previous prompt. The
	 * performed action can be different from the player's requested action
	 * if, for example, the player attempts to raise below the minimum.
	 *
	 * @param performedAction
	 */
	void performed(Action performedAction);

	/**
	 * @return Player's name.
	 */
	String getName();

	/**
	 * Informs the player of having won something from the pot.
	 */
	void won(int chips);
}
