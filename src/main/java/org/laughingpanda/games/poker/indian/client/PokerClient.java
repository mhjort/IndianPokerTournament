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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcHandler;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class PokerClient {

	public static void main(String[] args) throws Exception {
		Configuration configuration = new ConfigurationReader().readConfiguration(System.in);
		Bot bot = createBotInstanceUsingDefaultConstructor(configuration.getBotClass());
		PokerClient.join(configuration.getServerHost().getName(), configuration.getServerHost().getPort(), configuration.getBotName(), bot,
				configuration.getBotHost().getName(), configuration.getBotHost().getPort());
		waitForAnyKey();
	}

	private static void waitForAnyKey() throws IOException {
		System.out.println("Press any key to stop bot.");
		System.in.read();
		System.out.println("Bot stopped.");
	}

	private static Bot createBotInstanceUsingDefaultConstructor(String botClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> botClass = Class.forName(botClassName);
		Bot bot = (Bot) botClass.getConstructor().newInstance();
		return bot;
	}

	public static void join(String serverHost, int serverPort, String botName, final Bot bot, String botHost,
			int botPort) {
		sendJoinMessage(serverHost, serverPort, botName, botHost, botPort);
		WebServer server = WebServers.forPort(botPort);
		server.addHandler("Bot", new DelegatingHandler(bot));
	}

	private static void sendJoinMessage(String host, int port, String botName, String botHost, int botPort) {
		Vector<Comparable> args = new Vector<Comparable>();
		try {
			args.add(botHost);
			args.add(botPort);
			args.add(botName);
			XmlRpcClient client = new XmlRpcClient(host, port);
			client.execute("Server.join", args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final class DelegatingHandler implements XmlRpcHandler {
		private final Bot bot;

		private Map<String, Delegator> delegators = new HashMap<String, Delegator>();

		private DelegatingHandler(Bot bot) {
			this.bot = bot;
			initDelegators();
		}

		public Object execute(String handlerName, Vector args) throws Exception {
			Delegator delegator = delegators.get(handlerName);
			return delegator.delegate(bot, args);
		}

		private void initDelegators() {
			delegators.put("Bot.tournamentStarts", new TournamentStartsDelegator());
			delegators.put("Bot.handStarts", new HandStartsDelegator());
			delegators.put("Bot.paidBlind", new PaidBlindDelegator());
			delegators.put("Bot.playerActed", new PlayerActedDelegator());
			delegators.put("Bot.action", new ActionDelegator());
			delegators.put("Bot.showCard", new ShowCardDelegator());
			delegators.put("Bot.performed", new PerformedDelegator());
			delegators.put("Bot.won", new WonDelegator());
		}
	}

	interface Delegator {
		Object delegate(Bot bot, Vector args);
	}

	abstract static class VoidDelegator implements Delegator {
		public Object delegate(Bot bot, Vector args) {
			delegateVoid(bot, args);
			return Boolean.TRUE;
		}

		abstract protected void delegateVoid(Bot bot, Vector args);
	}

	static class TournamentStartsDelegator extends VoidDelegator {
		@Override
		protected void delegateVoid(Bot bot, Vector args) {
			Integer initialStack = (Integer) args.get(0);
			String[] playerNames = (String[]) ((Vector) args.get(1)).toArray(new String[0]);
			bot.tournamentStarts(initialStack, playerNames);
		}
	}

	static class HandStartsDelegator extends VoidDelegator {
		@Override
		public void delegateVoid(Bot bot, Vector args) {
			String dealer = (String) args.get(0);
			Hashtable<String, Integer> blinds = (Hashtable<String, Integer>) args.get(1);
			Hashtable<String, String> cards = (Hashtable<String, String>) args.get(2);
			Hashtable<String, Integer> stacks = (Hashtable<String, Integer>) args.get(3);
			bot.handStarts(dealer, blinds, cards, stacks);
		}
	}

	static class PaidBlindDelegator extends VoidDelegator {
		@Override
		public void delegateVoid(Bot bot, Vector args) {
			Integer chips = (Integer) args.get(0);
			bot.paidBlind(chips);
		}
	}

	static class PlayerActedDelegator extends VoidDelegator {
		@Override
		public void delegateVoid(Bot bot, Vector args) {
			String player = (String) args.get(0);
			String action = (String) args.get(1);
			bot.playerActed(player, action);
		}
	}

	static class ActionDelegator implements Delegator {
		public Object delegate(Bot bot, Vector args) {
			int priceToCall = (Integer) args.get(0);
			int minimumRaise = (Integer) args.get(1);
			int potSize = (Integer) args.get(2);
			return bot.action(priceToCall, minimumRaise, potSize);
		}
	}

	static class ShowCardDelegator extends VoidDelegator {
		@Override
		public void delegateVoid(Bot bot, Vector args) {
			String card = (String) args.get(0);
			bot.showCard(card);
		}
	}

	static class PerformedDelegator extends VoidDelegator {
		@Override
		protected void delegateVoid(Bot bot, Vector args) {
			String performedAction = (String) args.get(0);
			bot.performed(performedAction);
		}
	}

	static class WonDelegator extends VoidDelegator {
		@Override
		protected void delegateVoid(Bot bot, Vector args) {
			int chips = (Integer) args.get(0);
			bot.won(chips);
		}
	}
}
