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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.laughingpanda.games.poker.indian.domain.HandListener;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.TournamentListener;
import org.laughingpanda.games.poker.indian.domain.events.BlindEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerPromptEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerWinEvent;
import org.laughingpanda.games.poker.indian.server.swing.Application;
import org.laughingpanda.games.poker.indian.utils.Format;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class SystemOutUI implements ServerUi, TournamentListener, HandListener {

	private final Server server;

	private IndianpokerCommentator[] commentators;

	private Application gui;

	public SystemOutUI(Server server, IndianpokerCommentator... commentators) {
		this.server = server;
		this.commentators = commentators;
		gui = new Application();
		server.registerUi(this);
	}

	public void start() {
		server.createTournament();
		String message = "Waiting for players to join... Press \"ENTER\" to start tournament.";
		output(message);
		waitForAnyKey();
		server.startTournament();
	}

	public List<HandListener> getHandListeners() {
		return Arrays.asList(new HandListener[] { this, gui.getHandListener() });
	}

	public List<TournamentListener> getTournamentListeners() {
		return Arrays.asList(new TournamentListener[] { this, gui.getTournamentListener() });
	}

	public void onPlayerJoin(Player player) {
		output("\"" + player.getName() + "\" has joined.");
	}

	public void onPlayerDrop(Player player) {
		output("\"" + player.getName() + "\" dropped out.");
	}

	public void onTournamentFinish(List<Player> results) {
		ArrayList<Player> players = new ArrayList<Player>(results);
		String winner = players.remove(0).getName();
		Collections.reverse(players);
		String loser = players.remove(0).getName();
		String message = loser + " ranked last in the tournament";
		int rank = players.size() + 1;
		for (Player player : players) {
			String name = Format.possessive(player.getName());
			message += ",\n" + name + " ranking was " + rank--;
		}
		message += ".\n" + winner + " has won the tournament.\nCongratulations " + winner + ". Let's give big round of applauses for " + winner + "!";
		output(message);
		quit();
	}

	public void onActionPrompt(PlayerPromptEvent e) {
	}

	public void onHandEnd(final Map<Player, Integer> wins) {
		List<Player> winners = new ArrayList<Player>(wins.keySet());
		Collections.sort(winners, new Comparator<Player>() {
			public int compare(Player a, Player b) {
				return wins.get(b).compareTo(wins.get(a));
			}
		});
		StringBuffer s = new StringBuffer();
		if (wins.size() > 1) {
			s.append("  The pot is split between ");
			s.append(Format.commaSeparated(namesOf(winners))).append(".\n");
		}
		for (Player p : winners) {
			s.append("  \"" + p.getName() + "\" wins " + Format.pluralize(wins.get(p), "chip") + ".\n");
		}
		output(s.toString());
	}

	private List<String> namesOf(List<Player> players) {
		List<String> names = new ArrayList<String>();
		for (Player p : players) {
			names.add(p.getName());
		}
		return names;
	}

	public void onHandStart(List<Player> players) {
		output("Another exciting hand starts.");
	}

	public void onPlayerAction(PlayerActionEvent e) {
		StringBuffer s = new StringBuffer();
		s.append("  \"" + e.player.getName() + "\" ");
		s.append(e.action.toSpokenActionString());
		s.append(".");
		output(s.toString());
	}

	public void onPlayerWin(PlayerWinEvent e) {
	}

	public void onShowdown() {
	}

	public void onSmallBlind(BlindEvent e) {
		playerOutput(e.player, "bets " + Format.pluralize(e.chips, "chip") + " as small blind.");
	}

	public void onBigBlind(BlindEvent e) {
		playerOutput(e.player, "bets " + Format.pluralize(e.chips, "chip") + " as big blind.");
	}

	private void waitForAnyKey() {
		try {
			System.in.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void playerOutput(Player p, String restOfMessage) {
		output("  \"" + p.getName() + "\" " + restOfMessage);
	}

	private void output(String message) {
		for (IndianpokerCommentator commentator : commentators) {
			commentator.say(message);
		}
	}

	protected void quit() {
		// System.exit(0);
	}
}
