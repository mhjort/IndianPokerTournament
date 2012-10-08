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

import java.util.ArrayList;
import java.util.List;


/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class FixedDeck implements Deck {

	private int index = 0;

	private List<Card> cards = new ArrayList<Card>();

	public int numberOfShuffles = 0;

	public void add(Card card) {
		cards.add(card);
	}

	public Card nextCard() {
		if (index >= cards.size()) {
			index = 0;
		}
		return cards.get(index++);
	}

	public void shuffle() {
		// don't really shuffle--it's a fixed deck!
		index = 0;
		numberOfShuffles++;
	}

	public Card card(int index) {
		return cards.get(index);
	}

}
