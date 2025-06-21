package com.example.erv.dto.report.individual;

import java.time.ZonedDateTime;

import com.example.erv.model.User;
import com.example.erv.specification.SearchDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IndividualReportRequest extends SearchDTO {
    @NotNull
    private User user;
    @NotNull
    private ZonedDateTime dateFrom;
    @NotNull
    private ZonedDateTime dateTo;
}
