package org.laughingpanda.games.poker.indian.utils;

/**
 * Utility for wrapping the real system clock.
 * 
 * @author lkoskela
 */
public class SystemTime {

	private static Source activeTimeSource = createDefaultSource();

	public static long millis() {
		return activeTimeSource.millis();
	}

	private static Source createDefaultSource() {
		return new RealSource();
	}

	public static void resetSource() {
		setSource(createDefaultSource());
	}

	public static void setSource(Source source) {
		SystemTime.activeTimeSource = source;
	}

	/**
	 * The interface for a time source.
	 */
	public interface Source {
		long millis();
	}

	/**
	 * The default, "production" implementation of a time source.
	 */
	private static class RealSource implements Source {

		public long millis() {
			return System.currentTimeMillis();
		}
	}

}
