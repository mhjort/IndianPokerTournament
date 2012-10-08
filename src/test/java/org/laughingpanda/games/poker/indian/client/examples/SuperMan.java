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
package org.laughingpanda.games.poker.indian.client.examples;

import java.util.Hashtable;
import java.util.TreeSet;

import org.laughingpanda.games.poker.indian.client.Bot;
import org.laughingpanda.games.poker.indian.client.PokerClient;


/**
 * @author Antti Mattila
 */
public class SuperMan implements Bot {
	private int highestRank;

	private int bigBlind;

	private boolean handStarted = false;

	public SuperMan() {
	}

	public String action(int priceToCall, int minimumRaise, int potSize) {
		String result;
		if (handStarted == false)
			result = "call";
		handStarted = false;
		if (highestRank < 8 && highestRank > 6)
			result = "call";
		if (highestRank < 7 && highestRank > 4)
			result = "raise by " + 2 * bigBlind;
		if (highestRank < 5)
			result = "raise by " + 8 * bigBlind;
		if (highestRank < 4)
			result = "raise by " + 20 * bigBlind;
		if (highestRank < 3)
			result = "raise by " + 50 * bigBlind;
		result = "fold";
		return result;
	}

	public void handStarts(String dealer, Hashtable<String, Integer> blinds, Hashtable<String, String> cards, Hashtable<String, Integer> stacks) {
		handStarted = true;
		bigBlind = new TreeSet<Integer>(blinds.values()).last();
		highestRank = getHighestCard(cards);
	}

	public int getHighestCard(Hashtable<String, String> cards) {
		TreeSet<Integer> ranks = new TreeSet<Integer>();
		for (String card : cards.values()) {
			int parseInt = 11;
			try {
				parseInt = Integer.parseInt(card.substring(0, 1));
			} catch (Exception e) {
			}
			ranks.add(parseInt);
		}
		return ranks.last();
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

	public static void main(String[] args) throws InterruptedException {
		PokerClient.join("194.111.80.178", 5000, "Pihhhvi Annttti", new SuperMan(), "", 6666);
		while (true) {
			Thread.sleep(100);
		}
	}
}
