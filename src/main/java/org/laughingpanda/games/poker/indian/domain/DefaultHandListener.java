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
 * A <tt>HandListener</tt> implementation providing default (no-op)
 * implementations of all event methods. Subclasses can override only those
 * event methods that they're interested in.
 *
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class DefaultHandListener implements HandListener {

	public void onActionPrompt(PlayerPromptEvent e) {
	}

	public void onBigBlind(BlindEvent e) {
	}

	public void onHandEnd(Map<Player, Integer> wins) {
	}

	public void onHandStart(List<Player> players) {
	}

	public void onPlayerAction(PlayerActionEvent e) {
	}

	public void onPlayerWin(PlayerWinEvent e) {
	}

	public void onShowdown() {
	}

	public void onSmallBlind(BlindEvent e) {
	}
}
