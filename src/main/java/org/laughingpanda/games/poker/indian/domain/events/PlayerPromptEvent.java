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
package org.laughingpanda.games.poker.indian.domain.events;

import org.laughingpanda.games.poker.indian.domain.Player;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class PlayerPromptEvent extends HandEvent {

	private Player player;

	private int priceToCall;

	private int minRaise;

	private int potSize;

	public PlayerPromptEvent(Player player, int priceToCall, int minRaise, int potSize) {
		this.player = player;
		this.priceToCall = priceToCall;
		this.minRaise = minRaise;
		this.potSize = potSize;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerPromptEvent)) {
			return false;
		}
		PlayerPromptEvent other = (PlayerPromptEvent) obj;
		return player.equals(other.player) && priceToCall == other.priceToCall && minRaise == other.minRaise && potSize == other.potSize;
	}

	@Override
	public String toString() {
		return player.getName() + "? in pot: " + player.totalBets() + " call:" + priceToCall + ", minraise:" + minRaise + ", pot:" + potSize;
	}
}