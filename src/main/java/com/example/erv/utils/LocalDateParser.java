package com.example.erv.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class LocalDateParser {
    // Updated regex to validate date-time with optional fractional seconds
    private static final String DATE_TIME_REGEX = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,9})?$";

    private LocalDateParser() {
    }

    public static LocalDate parse(String dateTimeString) {
        if (!matches(dateTimeString)) {
            throw new IllegalArgumentException("Invalid date format: " + dateTimeString);
        }

        // Define formatter to parse the string
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
                .optionalEnd()
                .toFormatter();

        try {
            // Parse the string as LocalDateTime and convert to LocalDate
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            return dateTime.toLocalDate(); // Convert to LocalDate
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateTimeString, e);
        }
    }

    public static boolean matches(String dateTimeString) {
        return dateTimeString.matches(DATE_TIME_REGEX);
    }
}
