package com.example.erv.dto.report.daily;

import java.time.LocalDate;
import java.util.List;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.specification.SearchDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DailyReportRequest extends SearchDTO {
    private User user;
    private LocalDate dateTime;
    private String recordNumber;
    private List<DepartmentUnit> departmentUnit;
}
