package com.example.erv.dto.report.checkinstats;

import java.time.ZonedDateTime;

import com.example.erv.exceptions.FieldNotFoundException;
import com.example.erv.specification.SearchDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CheckInStatsReportRequest extends SearchDTO {
    @NotNull
    private ZonedDateTime dateFrom;
    @NotNull
    private ZonedDateTime dateTo;

    @Override
    protected void checkFilters(Class<?> clazz) throws FieldNotFoundException {
        // skip checking filters because this is a custom request.
    }
}
