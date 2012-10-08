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
package org.laughingpanda.games.poker.indian.utils;

import java.util.List;
import java.util.Map;

import org.laughingpanda.games.poker.indian.domain.HandListener;
import org.laughingpanda.games.poker.indian.domain.Player;
import org.laughingpanda.games.poker.indian.domain.events.BlindEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerActionEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerPromptEvent;
import org.laughingpanda.games.poker.indian.domain.events.PlayerWinEvent;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class LoggingHandListener implements HandListener {

	private int handNumber = 0;

	public void onActionPrompt(PlayerPromptEvent e) {
		System.out.print("  " + e + "... ");
	}

	public void onBigBlind(BlindEvent e) {
		System.out.println("  " + e);
	}

	public void onHandEnd(Map<Player, Integer> wins) {
		System.out.println("Hand #" + handNumber + " ends");
	}

	public void onHandStart(List<Player> players) {
		handNumber++;
		System.out.println("Hand #" + handNumber + " starts");
	}

	public void onPlayerAction(PlayerActionEvent e) {
		System.out.println("  " + e);
	}

	public void onPlayerWin(PlayerWinEvent e) {
		System.out.println("  " + e);
	}

	public void onShowdown() {
		System.out.println("  Showdown!");
	}

	public void onSmallBlind(BlindEvent e) {
		System.out.println("  " + e);
	}

}