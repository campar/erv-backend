package com.example.erv.dto.report.pause;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PauseReportResponse {
	private LocalDate date;
	private String duration;
	private List<TimetableInfo> timetableInfo = new ArrayList<>();
}
