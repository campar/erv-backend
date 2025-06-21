package com.example.erv.dto.report.individual;

import java.time.ZonedDateTime;

import com.example.erv.model.Device;
import com.example.erv.model.Location;
import com.example.erv.model.enums.ActionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableInfo {
    private ZonedDateTime time;
    private ActionType actionType;
    private Location location;
    private Device device;
}