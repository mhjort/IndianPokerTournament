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

import java.util.ArrayList;
import java.util.List;

import org.laughingpanda.games.poker.indian.domain.BlindsStrategy;
import org.laughingpanda.games.poker.indian.domain.Deck;
import org.laughingpanda.games.poker.indian.domain.DefaultDeck;
import org.laughingpanda.games.poker.indian.domain.FixedBlinds;
import org.laughingpanda.games.poker.indian.domain.TimeBasedBlinds;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class Configuration {

	private static int smallBlind = 1;

	private static int bigBlind = 2;

	private static int startingStack = 100;

	public static int getSmallBlind() {
		return smallBlind;
	}

	public static int getBigBlind() {
		return bigBlind;
	}

	public static int getStartingStack() {
		return startingStack;
	}

	public static Deck getDeck() {
		return new DefaultDeck();
	}

	public static BlindsStrategy getBlindsStrategy() {
		List<BlindsStrategy> levels = new ArrayList<BlindsStrategy>();
		levels.add(fixed(1, 2));
		levels.add(fixed(2, 4));
		levels.add(fixed(4, 8));
		levels.add(fixed(8, 16));
		levels.add(fixed(15, 30));
		levels.add(fixed(20, 40));
		levels.add(fixed(30, 60));
		levels.add(fixed(40, 80));
		levels.add(fixed(50, 100));
		levels.add(fixed(75, 150));
		levels.add(fixed(100, 200));
		levels.add(fixed(200, 400));
		levels.add(fixed(400, 800));
		levels.add(fixed(800, 1600));
		BlindsStrategy[] levelsAsArray = levels.toArray(new BlindsStrategy[levels.size()]);
		return new TimeBasedBlinds(60*1000, levelsAsArray);
	}

	private static BlindsStrategy fixed(int small, int big) {
		return new FixedBlinds(small, big);
	}

}
