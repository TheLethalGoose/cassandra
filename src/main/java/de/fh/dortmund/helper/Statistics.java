package de.fh.dortmund.helper;

import java.util.Arrays;

public class Statistics {

    public static long calculateMedian(long[] numbers) {
        Arrays.sort(numbers);

        int length = numbers.length;
        int middle = length / 2;

        if (length % 2 == 0) {
            return (numbers[middle - 1] + numbers[middle]) / 2L;
        } else {
            return numbers[middle];
        }
    }
    public static long calculateAverage(long[] numbers) {
        long sum = 0;
        for (long number : numbers) {
            sum += number;
        }
        return sum / numbers.length;
    }
    public static long max(long[] numbers) {
        long max = 0;
        for (long number : numbers) {
            if (number > max) {
                max = number;
            }
        }
        return max;
    }
    public static long min(long[] numbers) {
        long min = Long.MAX_VALUE;
        for (long number : numbers) {
            if (number < min) {
                min = number;
            }
        }
        return min;
    }

}
