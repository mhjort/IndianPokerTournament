package org.laughingpanda.games.poker.indian.client;

import java.util.Hashtable;

/**
 * Base class for implementing a bot. The only abstract method that bots
 * extending from this class must implement is the "action" method. All other
 * methods have default (no-op) implementations.
 * 
 * @author lkoskela
 */
public abstract class AbstractBot implements Bot {

	public abstract String action(int priceToCall, int minimumRaise, int potSize);

	public void handStarts(String dealer, Hashtable<String, Integer> blinds, Hashtable<String, String> cards,
			Hashtable<String, Integer> stacks) {
	}

	public void paidBlind(int chips) {
	}

	public void performed(String performedAction) {
	}

	public void playerActed(String player, String action) {
	}

	public void showCard(String card) {
	}

	public void tournamentStarts(int initialStack, String[] playerNames) {
	}

	public void won(int chips) {
	}
}
