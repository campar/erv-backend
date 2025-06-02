package com.example.erv.dto.report.absence;

import com.example.erv.exceptions.FieldNotFoundException;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.specification.SearchDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AbsenceReportRequest extends SearchDTO {
    private User user;
    @NotNull
    private LocalDate date;
    private List<DepartmentUnit> departmentUnit;

    @Override
    protected void checkFilters(Class<?> clazz) throws FieldNotFoundException {
        // skip checking filters because this is a custom request.
    }
}
