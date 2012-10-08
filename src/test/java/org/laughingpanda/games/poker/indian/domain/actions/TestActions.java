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
package org.laughingpanda.games.poker.indian.domain.actions;

import org.laughingpanda.games.poker.indian.domain.actions.Call;
import org.laughingpanda.games.poker.indian.domain.actions.Check;
import org.laughingpanda.games.poker.indian.domain.actions.Fold;
import org.laughingpanda.games.poker.indian.domain.actions.Raise;

import junit.framework.TestCase;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class TestActions extends TestCase {

	public void testAllFoldsAreEqual() throws Exception {
		assertTrue(new Fold().equals(new Fold()));
	}

	public void testAllChecksAreEqual() throws Exception {
		assertTrue(new Check().equals(new Check()));
	}

	public void testCallsOfSamePriceAreEqual() throws Exception {
		assertTrue(new Call(2).equals(new Call(2)));
		assertFalse(new Call(2).equals(new Call(3)));
	}

	public void testRaisesAreEqualOnlyIfTheRaiseAmountAndPriceToCallMatches() throws Exception {
		assertTrue(new Raise(1, 5).equals(new Raise(1, 5)));
		assertFalse(new Raise(1, 5).equals(new Raise(1, 15)));
		assertFalse(new Raise(1, 5).equals(new Raise(15, 5)));
	}

	public void testToStringMethodsReturnSomethingSensible() throws Exception {
		assertEquals("fold", new Fold().toString());
		assertEquals("call", new Call(4).toString());
		assertEquals("check", new Check().toString());
		assertEquals("raise by 3", new Raise(0, 3).toString());
		assertEquals("call 2 and raise by 3", new Raise(2, 3).toString());
	}
}
