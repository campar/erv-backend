package com.example.erv;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DataSeederUtils {
	private static final Random random = new Random();

    public static <T> T getRandomObject(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return null; // or throw an exception if you prefer
        }
        int randomIndex = random.nextInt(objects.size());
        return objects.get(randomIndex);
    }
    
    public static <T> List<T> getEightyPercentOfObjects(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList(); // return an empty list if the input is null or empty
        }

        int targetSize = (int) (objects.size() * 0.8);
        List<T> shuffledList = new ArrayList<>(objects);
        Collections.shuffle(shuffledList);

        return shuffledList.subList(0, targetSize);
    }
    
    public static boolean returnTrueForPercentOfTime(Integer percent) {
        return random.nextInt(100) < percent;
    }
    
    public static int getRandomNumberInRange(int number, int range) {
        int min = number - range;
        int max = number + range;
        return random.nextInt((max - min) + 1) + min;
    }
    
    public static Instant getRandomDateTimeInRange(LocalDate date, int hours, int timeRangeInMinutes) {
        // Define the start and end time for the range
        LocalTime startTime = LocalTime.of(hours, 0).minusMinutes(timeRangeInMinutes / 2);
        LocalTime endTime = LocalTime.of(hours, 0).plusMinutes(timeRangeInMinutes / 2);

        // Convert LocalTime to LocalDateTime for the given date
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        // Generate a random LocalDateTime within the specified range
        long start = startDateTime.toEpochSecond(java.time.ZoneOffset.UTC);
        long end = endDateTime.toEpochSecond(java.time.ZoneOffset.UTC);

        long randomEpochSecond = ThreadLocalRandom.current().nextLong(start, end + 1);
        return Instant.ofEpochSecond(randomEpochSecond);
//        return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, java.time.ZoneOffset.UTC);
    }
    
    public static int getRandomNumberBetween(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min should be less than or equal to max");
        }
        return random.nextInt((max - min) + 1) + min;
    }
    
    public static Set<Integer> generateUniqueEightDigitNumbers(int count) {
    	int MIN_VALUE = 10000000;
    	int MAX_VALUE = 99999999;
        if (count > (MAX_VALUE - MIN_VALUE + 1)) {
            throw new IllegalArgumentException("Request exceeds the number of unique eight-digit numbers available.");
        }

        Random random = new Random();
        Set<Integer> uniqueNumbers = new HashSet<>();

        while (uniqueNumbers.size() < count) {
            int number = random.nextInt((MAX_VALUE - MIN_VALUE) + 1) + MIN_VALUE;
            uniqueNumbers.add(number);
        }

        return uniqueNumbers;
    }
    
}
