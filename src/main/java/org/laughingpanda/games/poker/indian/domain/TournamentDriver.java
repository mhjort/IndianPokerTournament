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
 * The <tt>TournamentDriver</tt> interface abstracts the execution of a game away
 * from the <tt>Tournament</tt> class itself, allowing for better control over a
 * tournament's life from one round to another.
 * <p>
 * In a production configuration, the <tt>TournamentDriver</tt> implementation would
 * typically invoke the given <tt>Tournament</tt> object's <tt>playNextRound()</tt>
 * method in a tight loop (in a daemon thread). In a test configuration, the
 * <tt>TournamentDriver</tt> can be controlled from a unit test, performing
 * assertions on the tournament's state in between rounds.
 *
 * @author Pekka Enberg
 * @author Markus Hjort
 * @author Lasse Koskela
 * @author Antti Mattila
 */
public interface TournamentDriver {
	void start(Tournament game);
}
