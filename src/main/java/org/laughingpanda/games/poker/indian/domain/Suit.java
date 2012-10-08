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
public enum Suit {
	SPADES("spades", "S"), CLUBS("clubs", "C"), HEARTS("hearts", "H"), DIAMONDS("diamonds", "D");

	private final String character;

	private final String name;

	private Suit(String name, String character) {
		this.name = name;
		this.character = character;
	}

	public String character() {
		return character;
	}

	@Override
	public String toString() {
		return name;
	}
}
