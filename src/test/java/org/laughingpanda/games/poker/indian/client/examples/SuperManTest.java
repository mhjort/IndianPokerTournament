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
 * @author Antti Mattila
 */
public class SuperManTest extends TestCase {
	private SuperMan man;

	@Override
	protected void setUp() throws Exception {
		man = new SuperMan();
	}

	public void testGetHighestCard() throws Exception {
		Hashtable<String, String> cards = new Hashtable<String, String>();
		cards.put("5H", "5H");
		cards.put("7H", "7H");
		cards.put("3H", "3H");
		assertEquals(7, man.getHighestCard(cards));
		cards.put("TH", "TH");
		cards.put("8H", "8H");
		cards.put("4H", "4H");
		assertEquals(11, man.getHighestCard(cards));
		cards.put("JH", "JH");
		cards.put("8H", "8H");
		cards.put("4H", "4H");
		assertEquals(11, man.getHighestCard(cards));
		cards.put("3H", "3H");
		cards.put("8H", "8H");
		cards.put("AH", "AH");
		assertEquals(11, man.getHighestCard(cards));
	}
}
