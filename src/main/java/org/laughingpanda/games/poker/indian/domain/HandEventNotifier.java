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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.laughingpanda.games.poker.indian.domain.actions.Action;
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
public class HandEventNotifier {

	private Set<HandListener> listeners = new HashSet<HandListener>();

	public void register(HandListener listener) {
		listeners.add(listener);
	}

	public void notifyListenersOfHandStarting(List<Player> players) {
		for (HandListener listener : listeners) {
			listener.onHandStart(players);
		}
	}

	public void notifyListenersOfPlayerAction(Player player, Action action, int chipsPaid) {
		for (HandListener listener : listeners) {
			listener.onPlayerAction(new PlayerActionEvent(player, action, chipsPaid));
		}
	}

	public void notifyListenersOfPlayerPrompt(Player player, int priceToCall, int minRaise, int potSize) {
		for (HandListener listener : listeners) {
			listener.onActionPrompt(new PlayerPromptEvent(player, priceToCall, minRaise, potSize));
		}
	}

	public void notifyListenersOfShowdown() {
		for (HandListener listener : listeners) {
			listener.onShowdown();
		}
	}

	public void notifyListenersOfHandEnding(Map<Player, Integer> wins) {
		for (HandListener listener : listeners) {
			listener.onHandEnd(wins);
		}
	}

	public void notifyListenersOfPlayerWinning(Player player, int chips) {
		for (HandListener listener : listeners) {
			listener.onPlayerWin(new PlayerWinEvent(player, chips));
		}
	}

	public void notifyListenersOfSmallBlind(Player player, int blind) {
		for (HandListener listener : listeners) {
			listener.onSmallBlind(new BlindEvent(player, blind));
		}
	}

	public void notifyListenersOfBigBlind(Player player, int blind) {
		for (HandListener listener : listeners) {
			listener.onBigBlind(new BlindEvent(player, blind));
		}
	}
}
