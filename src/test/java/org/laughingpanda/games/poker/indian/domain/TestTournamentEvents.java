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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestTournamentEvents extends TestCase implements TournamentListener {

	private List<Player> actualResults;

	private List<Player> droppedPlayers;

	private int onPlayerJoinCount;

	private Set<Player> joinedPlayers;

	private Tournament tournament;

	private LoopingPlayer p1;

	private LoopingPlayer p2;

	private LoopingPlayer p3;

	@Override
	protected void setUp() throws Exception {
		joinedPlayers = new HashSet<Player>();
		droppedPlayers = new ArrayList<Player>();
		onPlayerJoinCount = 0;

		FixedDeck deck = new FixedDeck();
		deck.add(new Card(Suit.CLUBS, 13));
		deck.add(new Card(Suit.CLUBS, 2));
		deck.add(new Card(Suit.CLUBS, 7));

		p1 = new LoopingPlayer("P1", new Call(0), new Call(0), new Call(0), new Fold());
		p2 = new LoopingPlayer("P2", new Call(0));
		p3 = new LoopingPlayer("P3", new Call(0));
		tournament = new Tournament();
		tournament.setBlinds(1, 2);
		tournament.setDeck(deck);
		tournament.setStartingStack(10);
		tournament.setDriver(new RealTournamentDriver());
		tournament.addHandListener(new DefaultHandListener());
		tournament.addTournamentListener(this);

		tournament.join(p1);
		tournament.join(p2);
		tournament.join(p3);
	}

	public void testTournamentResultsAreGivenWinnerFirst() throws Exception {
		tournament.start();
		assertEquals(namesOf(list(p1, p3, p2)), namesOf(actualResults));
	}

	public void testTournamentNotifiesOfPlayerJoining() throws Exception {
		assertEquals(3, onPlayerJoinCount);
		assertTrue(joinedPlayers.contains(p1));
		assertTrue(joinedPlayers.contains(p2));
		assertTrue(joinedPlayers.contains(p3));
	}

	public void testTournamentNotifiesOfPlayerDroppingOut() throws Exception {
		tournament.start();
		assertEquals(list(p2, p3), droppedPlayers);
	}

	private List<String> namesOf(List<Player> players) {
		List<String> names = new ArrayList<String>();
		for (Player p : players) {
			names.add(p.getName());
		}
		return names;
	}

	private List<Player> list(Player... players) {
		return Arrays.asList(players);
	}

	public void onPlayerJoin(Player player) {
		joinedPlayers.add(player);
		onPlayerJoinCount++;
	}

	public void onTournamentFinish(List<Player> results) {
		this.actualResults = results;
	}

	public void onPlayerDrop(Player player) {
		droppedPlayers.add(player);
	}
}
