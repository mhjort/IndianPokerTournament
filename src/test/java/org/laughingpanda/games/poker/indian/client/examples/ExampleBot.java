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

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Random;

import org.laughingpanda.games.poker.indian.client.Bot;
import org.laughingpanda.games.poker.indian.client.PokerClient;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class ExampleBot implements Bot {
	public static void main(String[] args) throws InterruptedException {
		PokerClient.join("localhost", Integer.parseInt(args[0]), args[1], new ExampleBot(), args[2], Integer.parseInt(args[3]));
		waitForAnyKey();
	}

	private static void waitForAnyKey() {
		try {
			System.in.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Random random;

	public ExampleBot() {
		random = new SecureRandom();
	}

	public String action(int priceToCall, int minimumRaise, int potSize) {
		switch (random.nextInt(3)) {
		case 0:
			return "call";
		case 1:
			return "raise by " + random.nextInt(potSize + 1);
		default:
			return priceToCall > 0 ? "fold" : "check";
		}
	}

	public void handStarts(String dealer, Hashtable<String, Integer> blinds, Hashtable<String, String> cards, Hashtable<String, Integer> stacks) {
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
