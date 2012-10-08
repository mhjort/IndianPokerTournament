package org.laughingpanda.games.poker.indian.utils;

import junit.framework.TestCase;

public class TestSystemTime extends TestCase {

	@Override
	protected void setUp() throws Exception {
		assertUsingRealSystemTime();
	}

	@Override
	protected void tearDown() throws Exception {
		SystemTime.resetSource();
	}

	private void assertUsingRealSystemTime() {
		long before = System.currentTimeMillis();
		long systemTime = SystemTime.millis();
		long after = System.currentTimeMillis();
		assertTrue("SystemTime seems to be behind real system time.", systemTime >= before);
		assertTrue("SystemTime seems to be ahead of real system time.", systemTime <= after);
	}

	public void testTimeSourceCanBeChanged() throws Exception {
		SystemTime.Source src = new SystemTime.Source() {
			public long millis() {
				return 135235L;
			}
		};
		SystemTime.setSource(src);
		assertEquals(src.millis(), SystemTime.millis());

		SystemTime.resetSource();
		assertUsingRealSystemTime();
	}
}
