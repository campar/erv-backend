package com.example.erv.dto.report.pause;

import java.time.ZonedDateTime;

import com.example.erv.model.Device;
import com.example.erv.model.Location;
import com.example.erv.model.enums.ActionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimetableInfo {
	private ActionType actionType;
	private ZonedDateTime time;
	private Location location;
	private Device device;
}
