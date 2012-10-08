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
package org.laughingpanda.games.poker.indian.server;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

import org.laughingpanda.games.poker.indian.domain.Card;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.PlayerProxy;
import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestingTournamentMain extends Thread {

	private static final Server server = new Server();

	private final String host;

	private final int port;

	public TestingTournamentMain(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public static void main(String[] args) throws InterruptedException {
		new TestingTournamentMain(getHost(args), getPort(args)).start();
		Thread.sleep(2000);
		createTestGame();
	}

	private static int getPort(String[] args) {
		if (args.length > 1) {
			return Integer.parseInt(args[1]);
		}
		return 5000;
	}

	private static String getHost(String[] args) {
		try {
			if (args.length > 0) {
				return InetAddress.getByName(args[0]).getCanonicalHostName();
			}
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		Connector connector = new XmlRpcConnector(host, port);
		connector.connectTo(server);
		connector.start();

		ServerUi ui = new SystemOutUI(server, new TextToSpeech());
		ui.start();
	}

	private static void createTestGame() {
		server.join(new RandomPlayerProxy("Kent Beck"));
		server.join(new RandomPlayerProxy("Bob Martin"));
		server.join(new RandomPlayerProxy("Ken Schwaber"));
		server.join(new RandomPlayerProxy("Mary Poppendieck"));
		server.join(new RandomPlayerProxy("Alistair Cockburn"));
		server.join(new RandomPlayerProxy("The Unabomber"));
		server.join(new RandomPlayerProxy("Elvis"));
	}

	private static class RandomPlayerProxy implements PlayerProxy {

		private String name;

		private Random random;

		public RandomPlayerProxy(String name) {
			this.name = name;
			random = new SecureRandom();
		}

		public Action action(int priceToCall, int minimumRaise, int potSize) {
			switch (random.nextInt(3)) {
			case 0:
				return new Call(priceToCall);
			case 1:
				return new Raise(priceToCall, random.nextInt(potSize + 1));
			default:
				return priceToCall > 0 ? new Fold() : new Check();
			}
		}

		public String getName() {
			return name;
		}

		public void handStarts(Player dealer, Map<Player, Integer> blinds, Map<Player, Card> cards, Map<Player, Integer> stacks) {
		}

		public void paidBlind(int chips) {
		}

		public void performed(Action performedAction) {
		}

		public void playerActed(Player player, Action action) {
		}

		public void showCard(Card card) {
		}

		public void tournamentStarts(int stack, String[] playerNames) {
		}

		public void won(int chips) {
		}
	}
}
