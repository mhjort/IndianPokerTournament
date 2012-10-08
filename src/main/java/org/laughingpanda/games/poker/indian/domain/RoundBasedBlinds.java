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

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class RoundBasedBlinds implements BlindsStrategy {

	private int interval;

	private int roundsSinceLastBlindLevelRaise;

	private int currentBlindLevel;

	private final BlindsStrategy[] blindLevels;

	public RoundBasedBlinds(int numberOfRoundsBeforeBump, BlindsStrategy... blindLevels) {
		this.interval = numberOfRoundsBeforeBump;
		this.blindLevels = blindLevels;
		this.roundsSinceLastBlindLevelRaise = 0;
		this.currentBlindLevel = 0;
	}

	public int small() {
		return currentLevel().small();
	}

	public int big() {
		return currentLevel().big();
	}

	public void handIsOver() {
		roundsSinceLastBlindLevelRaise++;
		if (roundsSinceLastBlindLevelRaise == interval) {
			roundsSinceLastBlindLevelRaise = 0;
			if (currentBlindLevel < (blindLevels.length - 1)) {
				currentBlindLevel++;
			}
		}
	}

	private BlindsStrategy currentLevel() {
		return blindLevels[currentBlindLevel];
	}
}
