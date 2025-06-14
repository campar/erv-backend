package com.example.erv.dto.report.leavingarrival;

import java.time.ZonedDateTime;
import java.util.List;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.Shift;
import com.example.erv.specification.SearchDTO;

import lombok.Data;

@Data
public class LeavingArrivalReportRequest extends SearchDTO {
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    private List<Shift> shifts;
	private List<DepartmentUnit> departmentUnit;
}
