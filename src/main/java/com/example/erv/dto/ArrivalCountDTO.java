package com.example.erv.dto;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArrivalCountDTO {
	private ZonedDateTime intervalStart;
    private int checkinCount;
}
