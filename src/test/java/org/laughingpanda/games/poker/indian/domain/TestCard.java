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

import javax.swing.ImageIcon;


import junitx.extensions.EqualsHashCodeTestCase;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 * @author Paolo Perrotta
 */
public class TestCard extends EqualsHashCodeTestCase {
	private Card aceOfSpades;

	private Card deuceOfClubs;

	private Card aceOfHearts;

	private Card kingOfHearts;

	public TestCard(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		aceOfSpades = new Card(Suit.SPADES, 1);
		deuceOfClubs = new Card(Suit.CLUBS, 2);
		aceOfHearts = new Card(Suit.HEARTS, 1);
		kingOfHearts = new Card(Suit.HEARTS, 13);
	}

	public void testCardsHaveSuitAndRank() {
		assertEquals(new Rank(1), aceOfSpades.rank());
		assertEquals(Suit.SPADES, aceOfSpades.suit());
		assertEquals(new Rank(2), deuceOfClubs.rank());
		assertEquals(Suit.CLUBS, deuceOfClubs.suit());
	}

	public void testBothSuitAndRankMustMatchInOrderForTwoCardsToBeEqual() {
		Card anotherAceOfSpades = new Card(Suit.SPADES, 1);
		assertTrue(aceOfSpades.equals(anotherAceOfSpades));
		assertFalse(aceOfSpades.equals(aceOfHearts));
		assertFalse(kingOfHearts.equals(aceOfHearts));
	}

	public void testCardsCanBeCompared() {
		assertTrue(aceOfHearts.compareTo(kingOfHearts) > 0);
		assertTrue(kingOfHearts.compareTo(kingOfHearts) == 0);
		assertTrue(kingOfHearts.compareTo(aceOfHearts) < 0);
	}

	public void testCardsPrintOutNicely() {
		for (Suit suit : Suit.values()) {
			assertEquals("A" + suit.character(), new Card(suit, 1).toString());
			assertEquals("K" + suit.character(), new Card(suit, 13).toString());
			assertEquals("Q" + suit.character(), new Card(suit, 12).toString());
			assertEquals("J" + suit.character(), new Card(suit, 11).toString());
			assertEquals("T" + suit.character(), new Card(suit, 10).toString());
			for (int r = 2; r < 10; r++) {
				String expected = String.valueOf(r) + suit.character();
				assertEquals(expected, new Card(suit, r).toString());
			}
		}
	}

	public void testCardsPrintInSpokenFormat() {
		assertSpokenAs("5 of hearts", Suit.HEARTS, 5);
		assertSpokenAs("6 of clubs", Suit.CLUBS, 6);
		assertSpokenAs("10 of clubs", Suit.CLUBS, 10);
		assertSpokenAs("Jack of spades", Suit.SPADES, 11);
		assertSpokenAs("Queen of diamonds", Suit.DIAMONDS, 12);
		assertSpokenAs("King of spades", Suit.SPADES, 13);
		assertSpokenAs("Ace of spades", Suit.SPADES, 1);
	}

	private void assertSpokenAs(String expected, Suit suit, int rank) {
		assertEquals(expected, new Card(suit, rank).toSpokenString());
	}

	public void testComparableImplementation() {
		Card five = new Card(Suit.CLUBS, 5);
		Card anotherFive = new Card(Suit.SPADES, 5);
		Card king = new Card(Suit.CLUBS, 13);
		Card ace = new Card(Suit.CLUBS, 1);
		assertTrue(five.compareTo(anotherFive) == 0);
		assertTrue(five.compareTo(king) < 0);
		assertTrue(five.compareTo(ace) < 0);
		assertTrue(ace.compareTo(five) > 0);
		assertTrue(king.compareTo(five) > 0);
	}

	public void testCardsHaveImages() {
		DefaultDeck deck = new DefaultDeck();
		assertEquals(52, deck.cards.size());
		for (Card card : deck.cards) {
			assertTrue(new ImageIcon(card.getImage()).getIconHeight() > 0);
		}
	}

	@Override
	protected Object createInstance() {
		return new Card(Suit.CLUBS, 1);
	}

	@Override
	protected Object createNotEqualInstance() {
		return new Card(Suit.CLUBS, 2);
	}
}
