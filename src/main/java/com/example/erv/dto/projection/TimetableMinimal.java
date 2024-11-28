package com.example.erv.dto.projection;

import java.time.Instant;

import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;

public interface TimetableMinimal {
	Long getId();
	ActionType getActionType();
	Instant getDateTime();
	User getUser();
}
