package de.fh.dortmund.helper;

public class Timer {
	private long startTime;
	private long endTime;

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void stop() {
		endTime = System.currentTimeMillis();
	}

	public long getElapsedTime() {
		stop();
		return endTime - startTime;
	}

	@Override
	public String toString() {

		long duration = getElapsedTime();

		if(duration > 60000) {
			return (duration / 60000) + "min";
		}

		if(duration > 1000) {
			return (duration / 1000 )+ "sek";
		}

		return duration + "ms";
	}
}
