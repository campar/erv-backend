package com.example.erv.dto.report.individual;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualReportResponse {
    LocalDate date;
    String timeSpent;
    private List<TimetableInfo> timetableInfo = new ArrayList<>();
}
