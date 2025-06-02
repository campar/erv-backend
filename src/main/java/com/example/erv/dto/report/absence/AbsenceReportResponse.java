package com.example.erv.dto.report.absence;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenceReportResponse {
    User user;
    DepartmentUnit departmentUnit;
    Boolean present;
}
