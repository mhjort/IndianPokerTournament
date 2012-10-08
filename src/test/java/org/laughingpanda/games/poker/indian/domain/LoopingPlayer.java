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

import org.laughingpanda.games.poker.indian.domain.actions.Action;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class LoopingPlayer extends MockPlayer {

	public LoopingPlayer(String name, Action... actions) {
		super(name);
		for (Action a : actions) {
			this.actions.add(a);
		}
	}

	@Override
	public Action action(int priceToCall, int minimumRaise, int potSize) {
		Action action = actions.removeFirst();
		actions.add(action);
		return action;
	}
}