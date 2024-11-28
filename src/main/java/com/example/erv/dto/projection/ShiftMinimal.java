package com.example.erv.dto.projection;

public interface ShiftMinimal {
	Long getId();
	String getStartFrom();
	String getStartTo();
	String getEndFrom();
	String getEndTo();
	Integer getDurationHours();
	Integer getDurationMinutes();
}
