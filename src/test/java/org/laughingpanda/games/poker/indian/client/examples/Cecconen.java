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

import org.laughingpanda.games.poker.indian.client.Bot;
import org.laughingpanda.games.poker.indian.client.PokerClient;


/**
 * @author Lasse Koskela
 */
public class Cecconen implements Bot {

	public static void main(String[] args) throws Exception {
		String serverHost = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String botName = args[2];
		String botHost = args[3];
		int botPort = Integer.parseInt(args[4]);

		Bot bot = new Cecconen();
		System.out.println("Connecting to " + serverHost + ":" + serverPort + " from " + botHost + ":" + botPort + " as \"" + botName + "\"");
		PokerClient.join(serverHost, serverPort, botName, bot, botHost, botPort);
		while (true) {
			Thread.sleep(1000);
		}
	}

	private int stack;

	private Hashtable<String, String> cards;

	public String action(int priceToCall, int minimumRaise, int potSize) {
		String smallCards = "234567";
		boolean someoneHasBigCard = false;
		for (String card : cards.values()) {
			if (!smallCards.contains(card.substring(0, 1))) {
				someoneHasBigCard = true;
			}
		}
		if (someoneHasBigCard) {
			return "call";
		} else {
			return "raise by " + stack;
		}
	}

	public void handStarts(String dealer, Hashtable<String, Integer> blinds, Hashtable<String, String> cards, Hashtable<String, Integer> stacks) {
		this.cards = cards;
		System.out.println("Cecconen received: " + cards);
	}

	public void paidBlind(int chips) {
	}

	public void performed(String performedAction) {
	}

	public void playerActed(String player, String action) {
	}

	public void showCard(String card) {
		System.out.println("Cecconen had a " + card);
	}

	public void tournamentStarts(int initialStack, String[] playerNames) {
		this.stack = initialStack;
	}

	public void won(int chips) {
	}
}
