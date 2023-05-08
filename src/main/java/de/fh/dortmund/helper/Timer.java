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

	public static String convertMilliSeconds(long ms){

		if(ms > 60000) {
			return (ms / 60000) + "min";
		}

		if(ms > 1000) {
			return (ms / 1000 )+ "sek";
		}

		return ms + "ms";
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
