package com.example.erv.dto.report.absence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenceReportPDFResponse {
	String user;
	String action;
	String timeSpent;
}