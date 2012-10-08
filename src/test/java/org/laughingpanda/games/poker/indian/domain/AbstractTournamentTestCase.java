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

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

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
public abstract class AbstractTournamentTestCase extends TestCase {
	protected Tournament tournament;

	protected List<MockPlayer> players;

	protected MockPlayer player1;

	protected MockPlayer player2;

	protected MockPlayer player3;

	protected FixedDeck deck;

	protected int startingStack = 1000;

	protected int smallBlind = 5;

	protected int bigBlind = smallBlind * 2;

	protected MockDriver driver;

	protected int minimumRaise;

	protected MockTournamentListener listener;

	@Override
	protected void setUp() throws Exception {
		deck = new FixedDeck();
		deck.add(new Card(Suit.SPADES, 3));
		deck.add(new Card(Suit.SPADES, 4));
		deck.add(new Card(Suit.SPADES, 5));
		deck.add(new Card(Suit.SPADES, 6));
		deck.add(new Card(Suit.SPADES, 7));
		deck.add(new Card(Suit.SPADES, 8));
		deck.add(new Card(Suit.SPADES, 9));

		driver = new MockDriver();

		tournament = new Tournament();
		listener = new MockTournamentListener();
		tournament.addTournamentListener(listener);
		tournament.setDriver(driver);
		tournament.setDeck(deck);
		tournament.setStartingStack(startingStack);
		tournament.setBlinds(smallBlind, bigBlind);

		player1 = new MockPlayer("MockPlayer1");
		player2 = new MockPlayer("MockPlayer2");
		player3 = new MockPlayer("MockPlayer3");

		players = new ArrayList<MockPlayer>();
		players.add(player1);
		players.add(player2);
		players.add(player3);
	}

	protected void playersHaveStack(int expectedStack, MockPlayer... players) {
		for (MockPlayer player : players) {
			player.hasStack(expectedStack);
		}
	}

	protected void check(Player player) {
		expect(player.action(eq(0), eq(minimumRaise), anyInt())).andReturn(new Check());
	}

	protected void call(Player player, int priceToCall) {
		expect(player.action(eq(priceToCall), eq(minimumRaise), anyInt())).andReturn(new Call(priceToCall));
		expect(player.getStack()).andReturn(Integer.MAX_VALUE);
		player.bet(priceToCall);
	}

	protected void fold(Player player) {
		expect(player.action(eq(bigBlind), eq(minimumRaise), anyInt())).andReturn(new Fold());
	}

	protected void raise(Player player, int raise) {
		Assert.assertTrue("Bug in test: Raise should be at least " + minimumRaise, raise >= minimumRaise);
		expect(player.action(eq(smallBlind), eq(minimumRaise), anyInt())).andReturn(new Raise(0, raise));
		player.bet(smallBlind + raise);
	}

	protected static class MockTournamentListener implements TournamentListener {
		int onPlayerJoinCount = 0;

		Set<Player> joinedPlayers = new HashSet<Player>();

		public void onPlayerJoin(Player player) {
			joinedPlayers.add(player);
			onPlayerJoinCount++;
		}

		public void onTournamentFinish(List<Player> results) {
		}

		public void onPlayerDrop(Player player) {
		}
	}
}
