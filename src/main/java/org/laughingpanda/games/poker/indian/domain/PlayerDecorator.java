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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class PlayerDecorator implements Player {

	private PlayerProxy proxy;

	private Card card;

	private String name;

	private int stack;

	private int totalBetsInHand;

	private List<Player> otherPlayers;

	public PlayerDecorator(String name, PlayerProxy proxy) {
		this.name = name;
		this.proxy = proxy;
	}

	public Action action(int priceToCall, int minimumRaise, int potSize) {
		Action action = proxy.action(priceToCall, minimumRaise, potSize);
		if (action instanceof Raise) {
			Raise raise = (Raise) action;
			if (priceToCall >= stack) {
				action = new Call(stack);
			} else if (raise.amount() + priceToCall > stack) {
				action = new Raise(priceToCall, stack - priceToCall);
			} else if (raise.amount() < minimumRaise) {
				action = new Raise(priceToCall, Math.min(minimumRaise, stack));
			}
		} else if ((action instanceof Check) && priceToCall > 0) {
			action = new Call(priceToCall);
		}
		proxy.performed(action);
		return action;
	}

	public void bet(int numberOfChips) {
		this.stack -= numberOfChips;
		totalBetsInHand += numberOfChips;
	}

	public String getName() {
		return name;
	}

	public void handStarts(Player dealer, Map<Player, Integer> blinds) {
		Map<Player, Integer> stacks = new HashMap<Player, Integer>();
		Map<Player, Card> cards = new HashMap<Player, Card>();
		for (Player p : otherPlayers) {
			stacks.put(p, p.getStack());
			if (!p.getName().equals(name)) {
				cards.put(p, p.getCard());
			}
		}
		proxy.handStarts(dealer, blinds, cards, stacks);
		totalBetsInHand = 0;
	}

	public void handEnded(Map<Player, Integer> winnings) {

	}

	public boolean hasChips() {
		return stack > 0;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public void showCard() {
		proxy.showCard(card);
	}

	public void tournamentBegins(int yourStack, List<Player> seatingOrder) {
		this.stack = yourStack;
		this.otherPlayers = seatingOrder;
		proxy.tournamentStarts(yourStack, getNamesOf(seatingOrder));
	}

	private String[] getNamesOf(List<Player> otherPlayers) {
		List<String> names = new ArrayList<String>();
		for (Player p : otherPlayers) {
			names.add(p.getName());
		}
		return names.toArray(new String[names.size()]);
	}

	public void won(int chips) {
		stack += chips;
		proxy.won(chips);
	}

	public int getStack() {
		return stack;
	}

	public int totalBets() {
		return totalBetsInHand;
	}

	public void playerActed(PlayerActionEvent e) {
		proxy.playerActed(e.player, e.action);
	}

	public void blind(int numberOfChips) {
		proxy.paidBlind(numberOfChips);
	}
}
