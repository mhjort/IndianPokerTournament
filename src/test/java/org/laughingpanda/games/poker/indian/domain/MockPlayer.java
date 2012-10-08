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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class MockPlayer implements Player, PlayerProxy {

	public int stack;

	private int totalBets;

	private int chipsWon;

	public List<Player> players;

	private Card card;

	private String name;

	LinkedList<Action> actions = new LinkedList<Action>();

	List<Action> executedActions = new ArrayList<Action>();

	private List<PlayerActionEvent> playerActionEvents = new ArrayList<PlayerActionEvent>();

	private int blindsPaid;

	private Map<Player, Integer> notifiedWinnings;

	public boolean shouldAlwaysCheck = false;

	public MockPlayer() {
		this("MockPlayer-" + (int) (Math.random() * 100000));
	}

	public MockPlayer(String name) {
		this(name, new Action[0]);
	}

	public MockPlayer(String name, Action... actions) {
		this.name = name;
		for (Action a : actions) {
			this.actions.add(a);
		}
	}

	@Override
	public String toString() {
		return "MockPlayer(name:" + name + ", card:" + card + ", bets:" + totalBets + ")";
	}

	public void tournamentBegins(int yourStack, List<Player> seatingOrder) {
		this.stack = yourStack;
		this.players = seatingOrder;
		this.totalBets = 0;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public void bet(int numberOfChips) {
		stack -= numberOfChips;
		totalBets += numberOfChips;
	}

	public String getName() {
		return name;
	}

	public void hasStack(int expectedStack) {
		Assert.assertEquals("Player " + getName() + " doesn't have the expected stack.", expectedStack, getStack());
	}

	public void foldNextHand() {
	}

	public void hasBet(int expectedTotal) {
		Assert.assertEquals(expectedTotal, totalBets);
	}

	public void handStarts(Player dealer, Map<Player, Integer> blinds) {
		chipsWon = 0;
		totalBets = 0;
	}

	public void showCard() {
	}

	public Action action(int priceToCall, int minimumRaise, int potSize) {
		Action action = new Fold();
		if (shouldAlwaysCheck)
			action = new Check();
		if (!actions.isEmpty()) {
			action = actions.poll();
		}
		executedActions.add(action);
		return action;
	}

	public void won(int chips) {
		this.chipsWon = chips;
		this.stack += chips;
	}

	public boolean hasChips() {
		return stack > 0;
	}

	public int getChipsWon() {
		return chipsWon;
	}

	public int getStack() {
		return stack;
	}

	public int totalBets() {
		return totalBets;
	}

	public static MockPlayer createBettingPlayer(int bets, Card card) {
		MockPlayer p1 = new MockPlayer();
		p1.setCard(card);
		p1.tournamentBegins(bets, null);
		p1.bet(bets);
		return p1;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void playerActed(PlayerActionEvent e) {
		playerActionEvents.add(e);
	}

	public void assertReceivedPlayerActionEvents(PlayerActionEvent... expectedEvents) {
		Assert.assertEquals(Arrays.asList(expectedEvents), playerActionEvents);
	}

	public void setStack(int stack) {
		this.stack = stack;
	}

	public void assertHasNotPaidBlind() {
		Assert.assertEquals("Player " + name + " was not supposed to have paid a blind.", 0, blindsPaid);
	}

	public void assertHasPaidBlind(int blind) {
		Assert.assertEquals("Wrong blind for Player " + name, blind, blindsPaid);
	}

	public void blind(int numberOfChips) {
		blindsPaid += numberOfChips;
	}

	public void handEnded(Map<Player, Integer> winnings) {
		this.notifiedWinnings = winnings;
	}

	public void gotWinningNotification(Map<Player, Integer> distribution) {
		Assert.assertNotNull("All players should receive a winning notification.", notifiedWinnings);
		Assert.assertEquals(distribution, notifiedWinnings);
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
}
