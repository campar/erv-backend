package com.example.erv.dto;

import com.example.erv.model.enums.ActionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimetableActionRequest {
	@NotBlank
	private String rfid;
	@NotNull
	private ActionType actionType;
}
