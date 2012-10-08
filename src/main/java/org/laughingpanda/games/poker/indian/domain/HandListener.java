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

import java.util.List;
import java.util.Map;

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
public interface HandListener {

	/**
	 * Notification of a new hand starting.
	 */
	void onHandStart(List<Player> players);

	/**
	 * Notification of a player being prompted for action.
	 */
	void onActionPrompt(PlayerPromptEvent e);

	/**
	 * Notification of a player carrying out an action.
	 */
	void onPlayerAction(PlayerActionEvent e);

	/**
	 * Notification of a betting round ending, i.e. a showdown.
	 */
	void onShowdown();

	/**
	 * Notification of a player winning chips from the hand's pot.
	 */
	void onPlayerWin(PlayerWinEvent e);

	/**
	 * Notification of a hand ending.
	 *
	 * @param wins
	 *                The pot's distribution among winning players.
	 */
	void onHandEnd(Map<Player, Integer> wins);

	/**
	 * Notification of a player paying the big blind.
	 */
	void onBigBlind(BlindEvent e);

	/**
	 * Notification of a player paying the small blind.
	 */
	void onSmallBlind(BlindEvent e);
}
