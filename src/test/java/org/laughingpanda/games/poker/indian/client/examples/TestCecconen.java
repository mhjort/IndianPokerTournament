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
 * @author Lasse Koskela
 */
public class TestCecconen extends TestCase {

	private Cecconen c;

	protected void setUp() throws Exception {
		c = new Cecconen();
		c.tournamentStarts(100, new String[] { "Cecconen", "Kale", "Urho" });
	}

	public void testCecconenGoesAllInIfNobodyHasHigherThanSeven() throws Exception {
		Hashtable<String, Integer> stacks = new Hashtable<String, Integer>();
		stacks.put("Cecconen", 100);
		stacks.put("Kale", 40);
		stacks.put("Urho", 45);

		Hashtable<String, String> cards = new Hashtable<String, String>();
		cards.put("Kale", "5S");
		cards.put("Urho", "2H");

		Hashtable<String, Integer> blinds = new Hashtable<String, Integer>();
		blinds.put("Cecconen", 1);
		blinds.put("Kale", 2);

		c.handStarts("Kale", blinds, cards, stacks);
		assertEquals("raise by 100", c.action(0, 5, 0));
	}

	public void testCecconenCallsSmallPricesIfSomeoneHasHigherThanSeven() throws Exception {
		Hashtable<String, Integer> stacks = new Hashtable<String, Integer>();
		stacks.put("Cecconen", 100);
		stacks.put("Kale", 40);
		stacks.put("Urho", 45);

		Hashtable<String, String> cards = new Hashtable<String, String>();
		cards.put("Kale", "8S");
		cards.put("Urho", "2H");

		Hashtable<String, Integer> blinds = new Hashtable<String, Integer>();
		blinds.put("Cecconen", 1);
		blinds.put("Kale", 2);

		c.handStarts("Kale", blinds, cards, stacks);
		assertEquals("call", c.action((int) (0.9 * 100), 5, 0));
	}
}
