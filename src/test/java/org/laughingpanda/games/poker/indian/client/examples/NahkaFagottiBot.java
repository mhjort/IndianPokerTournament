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

import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import org.laughingpanda.games.poker.indian.client.Bot;
import org.laughingpanda.games.poker.indian.client.PokerClient;


/**
 * @author Markus Hjort
 */
public final class NahkaFagottiBot implements Bot {
	private Random random;
	private Hashtable<String, String> opponentCards = new Hashtable<String, String>();

	public NahkaFagottiBot() {
		random = new SecureRandom();
	}

	public static void main(String[] args) throws InterruptedException {
		PokerClient.join("194.111.80.178", 5000, "Nahkafagotti", new NahkaFagottiBot(), "194.111.80.182", 8077);
		while (true) {
			Thread.sleep(100);
		}
	}

	private boolean shouldRaiseOrCall() {
		return random.nextInt(100) < getRaiseOrCallPossibility();
	}

	public String action(int priceToCall, int minimumRaise, int potSize) {
		if (shouldRaiseOrCall()) {
			return "raise by " + random.nextInt(potSize + 1);
		}
		if (shouldRaiseOrCall())
			return "call";
		return priceToCall > 0 ? "fold" : "check";
	}

	private int raisePossibility;

	public void handStarts(String dealer, Hashtable<String, Integer> blinds, Hashtable<String, String> cards, Hashtable<String, Integer> stacks) {
		opponentCards  = cards;
		raisePossibility = 20;
		System.out.println("Hand starts!");
	}

	public void paidBlind(int chips) {
	}

	public void performed(String performedAction) {
	}

	public void playerActed(String player, String action) {
		if ("fold".equals(action))
			raisePossibility += 10;
	}

	public void showCard(String card) {
	}

	public void tournamentStarts(int initialStack, String[] playerNames) {
	}

	public void won(int chips) {
	}

	public int getRaiseOrCallPossibility() {
		Enumeration<String> cards = opponentCards.elements();
		while (cards.hasMoreElements()) {
			String card = cards.nextElement();
			if (isHighCard(card))
				return raisePossibility;
		}
		return raisePossibility + 30;
	}

	private boolean isHighCard(String card) {
		char rank = card.toUpperCase().charAt(0);
		if (rank == 'A' || rank == 'K' || rank == 'Q' || rank == 'J') {
			return true;
		}
		return false;
	}
}