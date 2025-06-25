package com.example.erv.dto.report.checkinstats;

import java.util.List;

import com.example.erv.model.Device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInStatsReportResponse {
    Device device;
    List<CheckIn> checkIns;
    Long total;
}
