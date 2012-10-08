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
package org.laughingpanda.games.poker.indian.client;

import java.util.Hashtable;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public interface Bot {

    /**
     * Notification of the tournament starting.
     * 
     * @param initialStack
     *            Your initial stack
     * @param playerNames
     *            The names of all players, indicating the seating order.
     */
    void tournamentStarts(int initialStack, String[] playerNames);

    /**
     * Notification of a new hand starting.
     * 
     * @param dealer
     *            The dealer.
     * @param blinds
     *            The blinds and their size in chips for the starting hand. E.g. {
     *            "Bob" => 3, "Charles" => 6 }
     * @param cards
     *            The cards of opponent players by player name. Card format is
     *            rank (2,3,4,5,6,7,8,9,T,J,Q,K,A) + suit (H,S,C,D). In other
     *            words, the ace of spades is "AS" and the deuce of clubs is
     *            "2C". Thus, the 'cards' parameter might look like, for
     *            example, this: { "Alice" => "AS", "Charles" => "2C" }
     * 
     * @param stacks
     *            The stacks of opponent players by player name. E.g. { "Alice" =>
     *            38, "Bob" => 40, "Charles" => 35 }
     */
    void handStarts(String dealer, Hashtable<String, Integer> blinds,
            Hashtable<String, String> cards,
            Hashtable<String, Integer> stacks);

    /**
     * A notification of another player's action.
     * 
     * @param player
     *            The other player.
     * @param action
     *            The other player's action. Actions: 'fold' Player folded
     *            'check' Player checked 'call' Player called 'raise by x'
     *            Player raised by x chips
     */
    void playerActed(String player, String action);

    /**
     * Prompts the player for her next action.
     * 
     * @param priceToCall
     *            The chips required for a "call"
     * @param minimumRaise
     *            The minimum raise (on top of <tt>priceToCall</tt>)
     * @param potSize
     *            The size of the pot before the player's action.
     * @return The player's action (one of "fold", "call", "check" and "raise by
     *         x")
     */
    String action(int priceToCall, int minimumRaise, int potSize);

    /**
     * Informs the player her card in showdown (if the player is in the
     * showdown).
     * 
     * @param card
     *            Card format is rank (2,3,4,5,6,7,8,9,T,J,Q,K,A) + suit
     *            (H,S,C,D) Examples: Ace of Spades => "AS", 2 of Hearts => "2H"
     */
    void showCard(String card);

    /**
     * Informs the player of paying a blind bet.
     */
    void paidBlind(int chips);

    /**
     * Indicates the action performed based on the previous prompt. The
     * performed action can be different from the player's requested action if,
     * for example, the player attempts to raise below the minimum.
     * 
     * @param performedAction
     *            One of "fold", "call", "check" and "raise by x"
     */
    void performed(String performedAction);

    /**
     * Informs the player of having won something from the pot.
     * 
     * @param chips
     *            The number of chips the player has won.
     */
    void won(int chips);
}
