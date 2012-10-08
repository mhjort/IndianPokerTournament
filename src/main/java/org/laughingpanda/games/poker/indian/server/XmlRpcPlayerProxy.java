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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.xmlrpc.XmlRpcClient;
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
 * @author Paolo Perrotta
 */
public class XmlRpcPlayerProxy implements PlayerProxy {

	private XmlRpcClient client;

	private final String name;

	public XmlRpcPlayerProxy(String name, String address, int port) {
		this.name = name;
		try {
			client = new XmlRpcClient(address, port);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		return name;
	}

	public void tournamentStarts(int stack, String[] playerNames) {
		rpcCall("Bot.tournamentStarts", stack, playerNames);
	}

	public void handStarts(Player dealer, Map<Player, Integer> blinds, Map<Player, Card> cards, Map<Player, Integer> stacks) {
		Hashtable<String, Integer> proxyBlinds = new Hashtable<String, Integer>();
		for (Player p : blinds.keySet()) {
			proxyBlinds.put(p.getName(), blinds.get(p));
		}

		Hashtable<String, String> proxyCards = new Hashtable<String, String>();
		for (Player p : cards.keySet()) {
			proxyCards.put(p.getName(), cards.get(p).toString());
		}

		Hashtable<String, Integer> proxyStacks = new Hashtable<String, Integer>();
		for (Player p : stacks.keySet()) {
			proxyStacks.put(p.getName(), stacks.get(p));
		}

		rpcCall("Bot.handStarts", dealer.getName(), proxyBlinds, proxyCards, proxyStacks);
	}

	public void paidBlind(int chips) {
		rpcCall("Bot.paidBlind", chips);
	}

	public Action action(int priceToCall, int minimumRaise, int potSize) {
        try {
            return unsafeAction(priceToCall, minimumRaise, potSize);
        } catch(Exception e) {
            return new Fold();
        }
    }

    private Action unsafeAction(int priceToCall, int minimumRaise, int potSize) throws Exception {
		String response = String.valueOf(rpcCall("Bot.action", priceToCall, minimumRaise, potSize));
		if (response.equals("check")) {
			return new Check();
		}
		if (response.equals("call")) {
			return new Call(priceToCall);
		}
		if (response.startsWith("raise by ")) {
			int raiseAmount = Integer.parseInt(response.substring("raise by ".length()));
			return new Raise(priceToCall, raiseAmount);
		}
		return new Fold();
	}

	public void performed(Action performedAction) {
		rpcCall("Bot.performed", performedAction.toString());
	}

	public void playerActed(Player player, Action action) {
		rpcCall("Bot.playerActed", player.getName(), action.toString());
	}

	public void showCard(Card card) {
		rpcCall("Bot.showCard", card.toString());
	}

	public void won(int chips) {
		rpcCall("Bot.won", chips);
	}

	private Object rpcCall(String handlerName, Object... args) {
		Vector<Object> vector = new Vector<Object>();
		for (Object arg : args) {
			vector.add(arg);
		}
		return rpcCall(handlerName, vector);
	}

	private Object rpcCall(final String handlerName, final Vector args) {
		final CountDownLatch clientReplied = new CountDownLatch(1);

		final List<Object> returnValue = new ArrayList<Object>();
		new Thread("RPC-CALL") {
			@Override
			public void run() {
				try {
					returnValue.add(client.execute(handlerName, args));
					clientReplied.countDown();
				} catch (Exception e) {
					System.out.println("WARNING: " + e.getMessage());
				}
			}
		}.start();
		try {
			if (clientReplied.await(5, TimeUnit.SECONDS)) {
				return returnValue.get(0);
			} else {
				System.out.println("WARNING: \"" + name + "\" didn't reply in time to " + handlerName);
				return null;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
