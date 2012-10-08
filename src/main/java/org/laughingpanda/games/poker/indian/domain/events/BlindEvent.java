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
public class BlindEvent extends HandEvent {

	public Player player;

	public int chips;

	public BlindEvent(Player player, int chips) {
		this.player = player;
		this.chips = chips;
	}

	@Override
	public String toString() {
		return player.getName() + " pays " + chips + " chips as a blind";
	}

	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		BlindEvent other = (BlindEvent) obj;
		return player.equals(other.player) && chips == other.chips;
	}
}
