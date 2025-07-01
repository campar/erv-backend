package com.example.erv.dto;

import java.time.ZonedDateTime;

import com.example.erv.model.enums.ActionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AttendanceRecordDTO {
	private String rfid;
	private ActionType actionType;
	private ZonedDateTime dateTime;
	private String deviceId;
}
