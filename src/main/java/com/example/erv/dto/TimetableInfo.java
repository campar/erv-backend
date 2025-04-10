package com.example.erv.dto;

import java.time.ZonedDateTime;

import com.example.erv.model.enums.ActionType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimetableInfo {
	private ActionType actionType;
	private ZonedDateTime  dateTime;
}
