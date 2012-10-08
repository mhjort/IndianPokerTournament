package org.laughingpanda.games.poker.indian.domain;

import org.laughingpanda.games.poker.indian.utils.SystemTime;

public class TimeBasedBlinds implements BlindsStrategy {

	private int currentBlindLevel = 0;

	private BlindsStrategy[] blindLevels;

	private long millisBetweenBumps;

	private long nextBumpAt = Long.MIN_VALUE;

	public TimeBasedBlinds(long amount, BlindsStrategy... blindLevels) {
		this.millisBetweenBumps = amount;
		this.blindLevels = blindLevels;
	}

	public int small() {
		setNextBumpIfNotSet();
		return currentLevel().small();
	}

	public int big() {
		return currentLevel().big();
	}

	public void handIsOver() {
		long now = SystemTime.millis();
		if (now >= nextBumpAt) {
			currentBlindLevel++;
			nextBumpAt = now + millisBetweenBumps;
		}
	}

	private BlindsStrategy currentLevel() {
		return blindLevels[Math.min(currentBlindLevel, blindLevels.length - 1)];
	}

	private void setNextBumpIfNotSet() {
		if (nextBumpAt == Long.MIN_VALUE) {
			nextBumpAt = SystemTime.millis() + millisBetweenBumps;
		}
	}

}
