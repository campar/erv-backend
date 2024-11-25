package com.example.erv.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.domain.Page;

import com.example.erv.config.AppConfig;
import com.example.erv.model.Timetable;
import com.example.erv.model.enums.ActionType;

public class EntityUtils {
    private EntityUtils() {
    }

    public static Map<LocalDate, List<Timetable>> groupByDay(Page<Timetable> data) {
        return groupByDay(data.toList());
    }

    public static Map<LocalDate, List<Timetable>> groupByDay(List<Timetable> data) {
        Map<LocalDate, List<Timetable>> byDay = new HashMap<>();
        List<Timetable> checkIns = data.stream().filter(e -> e.getActionType() == ActionType.CHECK_IN).toList();
        for (Timetable timetable : checkIns) {
            List<Timetable> groupActions = data.stream().filter(e -> e.getGroupingKey().equals(timetable.getGroupingKey())).toList();
            byDay.put(timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).toLocalDate(), groupActions);
        }
        return byDay;
    }

    public static Map<LocalDate, List<Timetable>> createTimetableDateRangeMap(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, List<Timetable>> dateMap = new HashMap<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dateMap.put(currentDate, new ArrayList<>());
            currentDate = currentDate.plusDays(1);
        }
        return dateMap;
    }

    public static Duration calculateDuration(List<Timetable> timetables, ActionType from, ActionType to) {
        List<Timetable> checkIn = timetables.stream().filter(t -> t.getActionType().equals(from)).toList();
        List<Timetable> checkOut = timetables.stream().filter(t -> t.getActionType().equals(to)).toList();
        if (checkIn.size() != 1 || checkOut.size() != 1) return null;

        return Duration.between(checkIn.get(0).getDateTime(), checkOut.get(0).getDateTime());
    }

    public static Map<ZonedDateTime, List<Timetable>> groupByMinutesInterval(List<Timetable> data, int intervalInMinutes) {
        Map<ZonedDateTime, List<Timetable>> groupedByInterval = new TreeMap<>();

        for (Timetable timetable : data) {
            ZonedDateTime roundedDateTime = timetable.getDateTime()
            		.atZone(ZoneId.of(AppConfig.getTimezoneId()))
                    .truncatedTo(ChronoUnit.MINUTES)
                    .withMinute((timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).getMinute() / intervalInMinutes) * intervalInMinutes);

            groupedByInterval.computeIfAbsent(roundedDateTime, k -> new ArrayList<>()).add(timetable);
        }
        return groupedByInterval;
    }
}
