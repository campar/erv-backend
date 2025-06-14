package com.example.erv.dto.report.leavingarrival;

import com.example.erv.model.Shift;
import com.example.erv.service.user.reports.TimetableActionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateTimetable {
    LocalDate timetableDate;
    List<TimetableActionData> timetables = new ArrayList<>();
    Shift shift;
}
