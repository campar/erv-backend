package com.example.erv.dto.report.leavingarrival;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimetableSnippet {
    private String time;
    private String action;
    private Boolean valid = true;
}
