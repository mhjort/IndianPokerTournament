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
package org.laughingpanda.games.poker.indian.tracer;

import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.Card;
import org.laughingpanda.games.poker.indian.domain.DefaultDeck;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.PlayerDecorator;
import org.laughingpanda.games.poker.indian.domain.PlayerProxy;
import org.laughingpanda.games.poker.indian.domain.Tournament;
import org.laughingpanda.games.poker.indian.domain.TournamentDriver;
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
public class TestRandomPlayers extends TestCase {

	public void test() throws Exception {
		Tournament tournament = new Tournament();
		tournament.setBlinds(1, 2);
		tournament.setDeck(new DefaultDeck());
		tournament.setStartingStack(100);
		tournament.setDriver(new TournamentDriver() {

			public void start(Tournament tournament) {
				while (!tournament.isFinished()) {
					tournament.playNextHand();
				}
			}
		});

		PlayerProxy p1 = new RandomPlayer();
		PlayerProxy p2 = new RandomPlayer();
		tournament.join(new PlayerDecorator("P1", p1));
		tournament.join(new PlayerDecorator("P2", p2));

		tournament.start();
	}

	private static class RandomPlayer implements PlayerProxy {

		public void showCard(Card card) {
		}

		public Action action(int priceToCall, int minimumRaise, int potSize) {
			int random = new Random().nextInt(4);
			Action action = null;
			switch (random) {
			case 0:
				action = new Fold();
				break;
			case 1:
				action = new Check();
				break;
			case 2:
				action = new Call(priceToCall);
				break;
			default:
				action = new Raise(priceToCall, new Random().nextInt(potSize * 2));
			}
			return action;
		}

		public void playerActed(Player player, Action action) {
		}

		public void tournamentStarts(int stack, String[] strings) {
		}

		public void handStarts(Player dealer, Map<Player, Integer> blinds, Map<Player, Card> cards, Map<Player, Integer> stacks) {
		}

		public void paidBlind(int chips) {
		}

		public void performed(Action performedAction) {
		}

		public String getName() {
			return "Random name";
		}

		public void won(int chips) {
		}
	}
}
