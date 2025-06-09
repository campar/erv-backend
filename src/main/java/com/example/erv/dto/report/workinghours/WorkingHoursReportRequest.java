package com.example.erv.dto.report.workinghours;

import java.time.ZonedDateTime;
import java.util.List;

import com.example.erv.exceptions.FieldNotFoundException;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.specification.SearchDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WorkingHoursReportRequest extends SearchDTO {
    private User user;
    @NotNull
    private ZonedDateTime dateFrom;
    @NotNull
    private ZonedDateTime dateTo;
    private List<DepartmentUnit> departmentUnit;

    @Override
    protected void checkFilters(Class<?> clazz) throws FieldNotFoundException {
        // skip checking filters because this is a custom request.
    }
}
