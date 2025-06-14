package com.example.erv.dto.report.leavingarrival;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.Shift;
import com.example.erv.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeavingArrivalReportResponse {
    User user;
    DepartmentUnit departmentUnit;
    List<Shift> shifts = new ArrayList<>();
    Integer faultsCount;
    List<DateTimetable> dateTimetables = new ArrayList<>();
}
