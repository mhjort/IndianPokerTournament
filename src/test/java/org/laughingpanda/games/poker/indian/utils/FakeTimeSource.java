package org.laughingpanda.games.poker.indian.utils;

public class FakeTimeSource implements SystemTime.Source {

	private long time;

	public FakeTimeSource(long initialTime) {
		time = initialTime;
	}

	public long millis() {
		return time;
	}

	public void advance(long millis) {
		time += millis;
	}

	public void set(long newTimeInMillis) {
		time = newTimeInMillis;
	}
}
