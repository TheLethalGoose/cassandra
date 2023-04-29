package de.fh.dortmund.helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

public class TimeStampGenerator {

	static long beginTime = Timestamp.valueOf("2020-01-01 00:00:00").getTime();
	static long endTime = Timestamp.valueOf("2023-04-29 23:59:59").getTime();
	static long diff = endTime - beginTime + 1;

	public static Timestamp generateRandom(){
		Random random = new Random();
		return new Timestamp(beginTime + (long) (random.nextDouble() * diff));
	}
	public static Timestamp now(){
		return Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
	}

}
