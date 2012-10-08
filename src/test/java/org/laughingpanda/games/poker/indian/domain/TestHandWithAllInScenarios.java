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

import java.util.Arrays;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestHandWithAllInScenarios extends TestCase {

	private MockPlayer player3;

	private MockPlayer player2;

	private MockPlayer player1;

	private Hand hand;

	private Action shouldNotBeCalledAction;

	@Override
	protected void setUp() throws Exception {
		shouldNotBeCalledAction = new Action() {
			@Override
			public String toString() {
				return "(should've not been called)";
			}
		};
		hand = new Hand();
		hand.setBlinds(1, 2);
		player1 = new MockPlayer("P1");
		player2 = new MockPlayer("P2");
		hand.setPlayers(Arrays.asList(new Player[] { player1, player2 }));
		hand.setDealer(player2);
		FixedDeck deck = new FixedDeck();
		deck.add(new Card(Suit.CLUBS, 1));
		deck.add(new Card(Suit.CLUBS, 2));
		deck.add(new Card(Suit.CLUBS, 3));
		hand.setDeck(deck);
	}

	public void testShouldNotCallPlayerWithNoChips() throws Exception {
		player1.stack = 3;
		player2.stack = 9;
		player1.actions.add(new Raise(1, 1));
		player1.actions.add(shouldNotBeCalledAction);
		player2.actions.add(new Raise(2, 2));
		hand.play();
		assertSame(shouldNotBeCalledAction, player1.actions.poll());
	}

	public void testBigBlindShouldNotBeAskedForActionIfSmallBlindIsAlreadyAllIn() throws Exception {
		player2.stack = 5; // dealer & big blind
		player1.stack = 1; // small blind
		player2.actions.add(shouldNotBeCalledAction);
		player1.actions.add(shouldNotBeCalledAction);
		hand.play();
		assertSame(shouldNotBeCalledAction, player1.actions.poll());
		assertSame(shouldNotBeCalledAction, player2.actions.poll());
	}

	public void testPlayerCanNotPayFullBigBlind() throws Exception {
		player1.stack = 5;
		player2.stack = 1;
		hand.play();
		assertEquals(1, player1.totalBets());
		assertEquals(1, player2.totalBets());
	}

	public void testPlayerCanNotPayFullSmallBlind() throws Exception {
		player1.stack = 1;
		player2.stack = 10;
		hand.setBlinds(2, 4);
		hand.play();
		assertEquals(1, player1.totalBets());
		assertEquals(4, player2.totalBets());
	}

	public void testNeitherPlayerCanNotPayTheirFullBlind() throws Exception {
		player1.stack = 1;
		player2.stack = 1;
		hand.setBlinds(2, 4);
		hand.play();
		assertEquals(1, player1.totalBets());
		assertEquals(1, player2.totalBets());
	}

	public void testAllPlayersAreAllIn() throws Exception {
		player1.stack = 9; // dealer & big blind
		player2.stack = 9; // small blind, first to act
		player2.actions.add(new Raise(1, 8));
		player1.actions.add(new Raise(8, 8));
		player2.actions.add(shouldNotBeCalledAction);
		player1.actions.add(shouldNotBeCalledAction);
		hand.play();
		assertEquals(shouldNotBeCalledAction, player1.actions.poll());
		assertEquals(shouldNotBeCalledAction, player2.actions.poll());
	}

	public void testAllThreePlayersAreAllIn() throws Exception {
		player3 = new MockPlayer("P3");
		hand.setPlayers(Arrays.asList(new Player[] { player1, player2, player3 }));
		player1.stack = 10; // big blind
		player2.stack = 20; // dealer, first to act
		player3.stack = 30; // small blind
		player2.actions.add(new Raise(2, 18)); // call the blind, go all-in
		player3.actions.add(new Raise(19, 10)); // pay the rest of the blind, call raise
		player1.actions.add(new Call(8));
		player1.actions.add(shouldNotBeCalledAction);
		player2.actions.add(shouldNotBeCalledAction);
		player3.actions.add(shouldNotBeCalledAction);
		hand.play();
		assertEquals(shouldNotBeCalledAction, player1.actions.poll());
		assertEquals(shouldNotBeCalledAction, player2.actions.poll());
		assertEquals(shouldNotBeCalledAction, player3.actions.poll());
		assertEquals(3 * 10, player1.getChipsWon());
		assertEquals(0, player2.getChipsWon());
		assertEquals(30, player3.getChipsWon());
		System.out.println(player1);
		System.out.println(player2);
		System.out.println(player3);
	}
}
