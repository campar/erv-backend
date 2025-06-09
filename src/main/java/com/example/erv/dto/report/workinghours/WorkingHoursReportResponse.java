package com.example.erv.dto.report.workinghours;

import com.example.erv.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursReportResponse {
    User user;
    String total;
}
