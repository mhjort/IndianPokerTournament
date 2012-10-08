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

import junit.framework.TestCase;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestRoundBasedBlindsStrategy extends TestCase {

	public void testBlindLevelsAreIncreasedUponReachingTheConfiguredNumberOfRounds() throws Exception {
		int interval = 3;
		BlindsStrategy strategy = new RoundBasedBlinds(interval, fixed(1, 2), fixed(2, 4), fixed(4, 8));
		for (int i = 0; i < interval; i++) {
			assertEquals(1, strategy.small());
			assertEquals(2, strategy.big());
			strategy.handIsOver();
		}
		for (int i = 0; i < interval; i++) {
			assertEquals(2, strategy.small());
			assertEquals(4, strategy.big());
			strategy.handIsOver();
		}
		assertEquals(4, strategy.small());
		assertEquals(8, strategy.big());
	}

	public void testBlindLevelsStopAtTheLastConfiguredLevel() throws Exception {
		int interval = 3;
		BlindsStrategy[] levels = { fixed(1, 2), fixed(2, 4), fixed(4, 8) };
		BlindsStrategy strategy = new RoundBasedBlinds(interval, levels);
		for (int i = 0; i < (interval * levels.length) + 10; i++) {
			strategy.handIsOver();
		}
		assertEquals(4, strategy.small());
		assertEquals(8, strategy.big());
	}

	private BlindsStrategy fixed(int small, int big) {
		return new FixedBlinds(small, big);
	}
}
