package org.laughingpanda.games.poker.indian.domain;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.laughingpanda.games.poker.indian.utils.FakeTimeSource;
import org.laughingpanda.games.poker.indian.utils.SystemTime;

public class TestTimeBasedBlindsStrategy extends TestCase {

	private BlindsStrategy strategy;

	private FakeTimeSource time;

	@Override
	protected void setUp() throws Exception {
		time = new FakeTimeSource(100000000);
		SystemTime.setSource(time);
	}

	@Override
	protected void tearDown() throws Exception {
		SystemTime.resetSource();
	}

	public void testBlindLevelsAreNotIncreasedIfTheClockDoesNotPassTheInterval() throws Exception {
		long interval = 5000;
		strategy = new TimeBasedBlinds(interval, fixed(1, 2), fixed(2, 4), fixed(4, 8));
		for (int i = 0; i < 4999; i++) {
			assertBlindsEqual(1, 2);
			strategy.handIsOver();
			time.advance(1);
		}
	}

	public void testBlindLevelsAreIncreasedOneHandAfterTheClockPassesTheNextInterval() throws Exception {
		long interval = 5000;
		strategy = new TimeBasedBlinds(interval, fixed(1, 2), fixed(2, 4), fixed(4, 8));

		assertBlindsEqual(1, 2);
		strategy.handIsOver();

		time.advance(interval);

		assertBlindsEqual(1, 2);
		strategy.handIsOver();

		assertBlindsEqual(2, 4);
		strategy.handIsOver();

		time.advance(interval);

		assertBlindsEqual(2, 4);
		strategy.handIsOver();

		assertBlindsEqual(4, 8);
		strategy.handIsOver();
	}

	private void assertBlindsEqual(int small, int big) {
		String msg = " blind doesn't match at " + SystemTime.millis();
		assertEquals("small" + msg, small, strategy.small());
		assertEquals("big" + msg, big, strategy.big());
	}

	private BlindsStrategy fixed(int small, int big) {
		return new FixedBlinds(small, big);
	}
}
