package com.example.erv.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class LocalDateTimeParser {
	private static final String DATE_TIME_REGEX = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,9})?$";

	private LocalDateTimeParser() {
	}

	public static LocalDateTime parse(String dateTimeString) {
		if (!matches(dateTimeString)) {
			throw new IllegalArgumentException("Invalid date format: " + dateTimeString);
		}

		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss").optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true).optionalEnd()
				.toFormatter();

		try {
			return LocalDateTime.parse(dateTimeString, formatter);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid date format: " + dateTimeString, e);
		}
	}

	public static boolean matches(String dateTimeString) {
		return dateTimeString.matches(DATE_TIME_REGEX);
	}
}
