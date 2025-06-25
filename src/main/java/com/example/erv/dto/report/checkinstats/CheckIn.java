package com.example.erv.dto.report.checkinstats;

import com.example.erv.model.enums.ActionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {
	ActionType actionType;
	Long count;
}
