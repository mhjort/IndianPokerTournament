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
package org.laughingpanda.games.poker.indian.utils;

import java.util.List;

/**
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public class Format {

	public static String pluralize(int count, String unit) {
		return "" + count + " " + unit + (count != 1 ? "s" : "");
	}

	public static String commaSeparated(List<? extends Object> names) {
		StringBuffer nameBuffer = new StringBuffer();
		for (int i = 0; i < names.size(); i++) {
			String name = String.valueOf(names.get(i));
			if (i > 0) {
				nameBuffer.append(i < names.size() - 1 ? ", " : " and ");
			}
			nameBuffer.append(name);
		}
		String commaSeparatedNames = nameBuffer.toString();
		return commaSeparatedNames;
	}

	public static String possessive(String name) {
		if (name.endsWith("s") || name.endsWith("S")) {
			return name + "'";
		} else {
			return name + "'s";
		}
	}

}
