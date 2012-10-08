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

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class Raise extends Action {

	private int amount;

	private int priceToCall;

	public Raise(int priceToCall, int raiseOnTop) {
		this.priceToCall = priceToCall;
		this.amount = raiseOnTop;
	}

	public int amount() {
		return amount;
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		Raise other = (Raise) obj;
		return amount == other.amount && priceToCall == other.priceToCall;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (priceToCall > 0) {
			s.append("call ").append(priceToCall).append(" and ");
		}
		s.append("raise by ").append(amount);
		return s.toString();
	}

	@Override
	public String toSpokenActionString() {
		return "raises by " + amount;
	}

	public void setCallPrice(int chips) {
		this.priceToCall = chips;
	}
}
