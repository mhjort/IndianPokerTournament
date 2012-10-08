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
package org.laughingpanda.games.poker.indian.client.examples;

import java.util.Hashtable;

import junit.framework.TestCase;

/**
 * @author Markus Hjort
 */
public class NahkaFagottiBotTest extends TestCase {
	private NahkaFagottiBot bot;

	private Hashtable<String, String> opponentCards;

	@Override
	protected void setUp() throws Exception {
		bot = new NahkaFagottiBot();
		opponentCards = new Hashtable<String, String>();
	}

	public void testRaisePossibilityIs50PercentWithNumberCards() throws Exception {
		opponentCards.put("player", "2H");
		opponentCards.put("player2", "3H");
		bot.handStarts("dealer", null, opponentCards, null);
		assertEquals(50, bot.getRaiseOrCallPossibility());
	}

	public void testRaisePossibilityIs20PercentWithHighCards() throws Exception {
		opponentCards.put("player", "JH");
		opponentCards.put("player2", "KH");
		bot.handStarts("dealer", null, opponentCards, null);
		assertEquals(20, bot.getRaiseOrCallPossibility());
	}

	public void testActionDoesWork() throws Exception {
		assertNotNull(bot.action(1, 1, 2));
	}
}
