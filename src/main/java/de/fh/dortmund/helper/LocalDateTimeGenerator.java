package de.fh.dortmund.helper;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LocalDateTimeGenerator {

	static LocalDateTime beginTime = LocalDateTime.parse("2020-01-01T00:00:00");
	static LocalDateTime endTime = LocalDateTime.parse("2023-04-29T23:59:59");

	public static LocalDateTime generateRandomLocalDateTime(){
		Duration duration = Duration.between(beginTime, endTime);
		long seconds = duration.getSeconds();
		long randomSeconds= ThreadLocalRandom.current().nextLong(seconds);
		return beginTime.plusSeconds(randomSeconds);
	}

	public static LocalDateTime generateRandomLocalDateTimeAfter(LocalDateTime after){
		Duration duration = Duration.between(after, endTime);
		long seconds = duration.getSeconds();
		long randomSeconds= ThreadLocalRandom.current().nextLong(seconds);
		return after.plusSeconds(randomSeconds);
	}

}
