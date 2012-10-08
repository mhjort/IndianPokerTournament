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

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 * @author Paolo Perrotta
 */
public class Card implements Comparable<Card> {

    public static final Image NONE = new ImageIcon(Card.class.getResource("/none.png")).getImage();
    public static final Image BACKWARDS = new ImageIcon(Card.class.getResource("/back.png")).getImage();

    private static HashMap<Card, ImageIcon> images = new HashMap<Card, ImageIcon>();
    
	private final Rank rank;
    
	private final Suit suit;

	public Card(Suit suit, int rank) {
		this.suit = suit;
        this.rank = new Rank(rank);
	}

	@Override
	public boolean equals(Object obj) {
		Card other = (Card) obj;
		return other != null && rank().equals(other.rank()) && suit.equals(other.suit);
	}

	@Override
	public int hashCode() {
		return new String(suit.toString() + rank.toString()).hashCode();
	}

	Rank rank() {
		return rank;
	}

	Suit suit() {
		return suit;
	}

	@Override
	public String toString() {
		return rank.toString() + suit.character();
	}

	private int rankWithAceAs14() {
		return rank.value();
	}

	public int compareTo(Card other) {
		return new Integer(rankWithAceAs14()).compareTo(other.rankWithAceAs14());
	}

	public String toSpokenString() {
		StringBuffer s = new StringBuffer();
		s.append(rank.toSpokenString());
		s.append(" of ");
		s.append(suit.toString());
		return s.toString();
	}

	public Image getImage() {
		ImageIcon icon = images.get(this);
		if (icon == null) {
			URL url = getClass().getResource("/" + suit.character() + rankWithAceAs14() + ".png");
			icon = new ImageIcon(url);
			images.put(this, icon);
		}
		return icon.getImage();
	}
}
