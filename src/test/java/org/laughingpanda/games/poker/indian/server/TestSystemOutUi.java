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

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.domain.Card;
import org.laughingpanda.games.poker.indian.domain.HandListener;
import org.laughingpanda.games.poker.indian.domain.MockPlayer;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.Suit;
import org.laughingpanda.games.poker.indian.domain.Tournament;
import org.laughingpanda.games.poker.indian.domain.TournamentListener;
import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;
import org.laughingpanda.games.poker.indian.domain.events.BlindEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerWinEvent;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestSystemOutUi extends TestCase {

	private MockSystemOutUI ui;

	private TournamentListener tournamentListener;

	private Player player1, player2, player3;

	private MockServer server;

	private PipedOutputStream fakeInput;

	private InputStream realSystemIn;

	protected boolean uiHasBeenStarted;

	protected boolean uiIsAboutToStart;

	private MockCommentator mockTts;

	private HandListener handListener;

	@Override
	protected void setUp() throws Exception {
		server = new MockServer();
		mockTts = new MockCommentator();
		ui = new MockSystemOutUI(server, mockTts);
		tournamentListener = ui.getTournamentListeners().get(0);
		handListener = ui.getHandListeners().get(0);

		player1 = new MockPlayer("P1");
		player2 = new MockPlayer("P2");
		player3 = new MockPlayer("P3");
		player1.setCard(new Card(Suit.HEARTS, 5));
		player2.setCard(new Card(Suit.CLUBS, 1));
		player3.setCard(new Card(Suit.CLUBS, 3));

		realSystemIn = System.in;
		fakeInput = new PipedOutputStream();
		System.setIn(new PipedInputStream(fakeInput));
	}

	@Override
	protected void tearDown() throws Exception {
		System.setIn(realSystemIn);
	}

	public void testUiShouldCreateTournamentOnStart() throws Exception {
		startUiInSeparateThread();
		giveUiInputToStartTournament();
		assertTrue(server.isCreateTournamentCalled);
	}

	public void testUiShouldRegisterItselfToServerOnInitialization() throws Exception {
		assertSame(ui, server.registeredUi);
	}

	public void testUiPrintsGameOnMessageOnStart() throws Exception {
		assertOutputEquals("");
		startUiInSeparateThread();
		waitForUiStartToBegin();
		assertOutputEquals("Waiting for players to join... Press \"ENTER\" to start tournament.\n");
	}

	public void testUiPrintsPlayerJoin() throws Exception {
		tournamentListener.onPlayerJoin(player1);
		assertOutputEquals("\"" + player1.getName() + "\" has joined.\n");
	}

	public void testUiStartsTournamentOnUserInput() throws Exception {
		startUiInSeparateThread();
		assertFalse(server.tournamentOngoing());
		giveUiInputToStartTournament();
		assertTrue(server.tournamentOngoing());
	}

	public void testUiShouldInformWhenHandStarts() throws Exception {
		handListener.onHandStart(list(player1, player2));
		StringBuffer expected = new StringBuffer();
		expected.append("Another exciting hand starts.").append("\n");
		assertOutputEquals(expected.toString());
	}

	public void testUiShouldInformWhenPlayerPaysSmallBlind() throws Exception {
		handListener.onSmallBlind(new BlindEvent(player1, 5));
		assertOutputEquals("  \"" + player1.getName() + "\" bets 5 chips as small blind.\n");
	}

	public void testUiShouldInformWhenPlayerPaysBigBlind() throws Exception {
		handListener.onBigBlind(new BlindEvent(player1, 5));
		assertOutputEquals("  \"" + player1.getName() + "\" bets 5 chips as big blind.\n");
	}

	public void testPluralizationOfBlinds() throws Exception {
		handListener.onSmallBlind(new BlindEvent(player1, 1));
		handListener.onBigBlind(new BlindEvent(player1, 1));
		handListener.onSmallBlind(new BlindEvent(player1, 2));
		handListener.onBigBlind(new BlindEvent(player1, 2));
		StringBuffer s = new StringBuffer();
		s.append("  \"" + player1.getName() + "\" bets 1 chip as small blind.\n");
		s.append("  \"" + player1.getName() + "\" bets 1 chip as big blind.\n");
		s.append("  \"" + player1.getName() + "\" bets 2 chips as small blind.\n");
		s.append("  \"" + player1.getName() + "\" bets 2 chips as big blind.\n");
		assertOutputEquals(s.toString());
	}

	public void testUiShouldIgnoreThePlayerWinEvent() throws Exception {
		handListener.onPlayerWin(new PlayerWinEvent(player1, 10));
		assertOutputEquals("");
	}

	public void testUiShouldInformWhenPotIsSplitNamingTheBiggestWinnerFirst() throws Exception {
		Map<Player, Integer> wins = new HashMap<Player, Integer>();
		wins.put(player1, 10);
		wins.put(player2, 30);
		wins.put(player3, 20);
		handListener.onHandEnd(wins);
		StringBuffer s = new StringBuffer();
		s.append("  The pot is split between ");
		s.append(player2.getName());
		s.append(", " + player3.getName());
		s.append(" and " + player1.getName());
		s.append(".\n");
		s.append("  \"" + player2.getName() + "\" wins 30 chips.\n");
		s.append("  \"" + player3.getName() + "\" wins 20 chips.\n");
		s.append("  \"" + player1.getName() + "\" wins 10 chips.\n");
		assertOutputEquals(s.toString());
	}

	public void testUiShouldNotSayAnythingAboutSplittingIfThePotIsNotSplit() throws Exception {
		Map<Player, Integer> wins = new HashMap<Player, Integer>();
		wins.put(player1, 10);
		handListener.onHandEnd(wins);
		assertOutputEquals("  \"" + player1.getName() + "\" wins 10 chips.");
	}

	public void testUiShouldInformWhenPlayerFolds() throws Exception {
		handListener.onPlayerAction(new PlayerActionEvent(player1, new Fold(), 0));
		assertOutputEquals("  \"" + player1.getName() + "\" folds.\n");
	}

	public void testUiShouldInformWhenPlayerChecks() throws Exception {
		handListener.onPlayerAction(new PlayerActionEvent(player1, new Check(), 0));
		assertOutputEquals("  \"" + player1.getName() + "\" checks.\n");
	}

	public void testUiShouldInformWhenPlayerCalls() throws Exception {
		handListener.onPlayerAction(new PlayerActionEvent(player1, new Call(15), 15));
		assertOutputEquals("  \"" + player1.getName() + "\" calls 15.\n");
	}

	public void testUiShouldInformWhenPlayerRaises() throws Exception {
		handListener.onPlayerAction(new PlayerActionEvent(player1, new Raise(0, 10), 10));
		assertOutputEquals("  \"" + player1.getName() + "\" raises by 10.\n");
	}

	public void testUiShouldInformWhenPlayerCallsAndRaises() throws Exception {
		handListener.onPlayerAction(new PlayerActionEvent(player1, new Raise(5, 10), 15));
		assertOutputEquals("  \"" + player1.getName() + "\" raises by 10.\n");
	}

	private List<Player> list(Player... players) {
		return Arrays.asList(players);
	}

	public void testUiShouldInformWhenPlayerDrops() throws Exception {
		tournamentListener.onPlayerDrop(player1);
		assertOutputEquals("\"" + player1.getName() + "\" dropped out.\n");
	}

	public void testUiInformsPlayerRanksAfterTournament() throws Exception {
		Player[] players = new MockPlayer[] { new MockPlayer("first"), new MockPlayer("second"), new MockPlayer("aguilus"), new MockPlayer("last") };
		tournamentListener.onTournamentFinish(Arrays.asList(players));
		assertOutputEquals("last ranked last in the tournament,\naguilus' ranking was 3,\nsecond's ranking was 2.\nfirst has won the tournament.\nCongratulations first. Let's give big round of applauses for first!");
	}

	public void testSystemUiQuitCalledWhenTournamentFinishes() throws Exception {
		assertFalse(ui.isQuitCalled);
		tournamentListener.onTournamentFinish(Arrays.asList(new Player[] { new MockPlayer(""), new MockPlayer("") }));
		assertTrue(ui.isQuitCalled);
	}

	private void giveUiInputToStartTournament() throws IOException {
		fakeInput.write(13);
		fakeInput.flush();
		waitForUserInput();
	}

	private void waitForUserInput() {
		while (!uiHasBeenStarted) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void waitForUiStartToBegin() {
		while (!uiIsAboutToStart) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void startUiInSeparateThread() {
		new Thread() {
			@Override
			public void run() {
				uiIsAboutToStart = true;
				ui.start();
				uiHasBeenStarted = true;
			}
		}.start();
	}

	private void assertOutputEquals(String expected) {
		String synthesizerOutput = mockTts.getOutput() + (expected.endsWith("\n") ? "\n" : "");
		assertEquals(expected.trim(), synthesizerOutput.trim());
	}

	public static class MockServer extends Server {

		ServerUi registeredUi = null;

		boolean isCreateTournamentCalled = false;

		boolean tournamentHasStarted = false;

		boolean tournamentOngoing() {
			return tournamentHasStarted;
		}

		@Override
		public void startTournament() {
			tournamentHasStarted = true;
		}

		@Override
		public void registerUi(ServerUi ui) {
			registeredUi = ui;
		}

		@Override
		public Tournament createTournament() {
			isCreateTournamentCalled = true;
			return null;
		}
	}

	private static class MockCommentator implements IndianpokerCommentator {

		StringBuffer said = new StringBuffer();

		public void say(String speech) {
			said.append(speech).append("\n");
		}

		public String getOutput() {
			return said.toString();
		}
	}

	private static class MockSystemOutUI extends SystemOutUI {
		boolean isQuitCalled = false;

		@Override
		public List<HandListener> getHandListeners() {
			ArrayList<HandListener> listener = new ArrayList<HandListener>();
			listener.add(this);
			return listener;
		}

		@Override
		public List<TournamentListener> getTournamentListeners() {
			ArrayList<TournamentListener> listener = new ArrayList<TournamentListener>();
			listener.add(this);
			return listener;
		}

		public MockSystemOutUI(Server server, IndianpokerCommentator tts) {
			super(server, tts);
		}

		@Override
		protected void quit() {
			isQuitCalled = true;
		}
	}
}
