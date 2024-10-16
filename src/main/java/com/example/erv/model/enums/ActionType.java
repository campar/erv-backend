package com.example.erv.model.enums;

public enum ActionType {

	CHECK_IN("Dolazak"),
	CHECK_OUT("Odlazak"),
	PAUSE_START("Početak pauze"),
	PAUSE_END("Kraj pauze"),
	BUSINESS_START("Službeni odlazak"),
	BUSINESS_END("Kraj službenog odlaska");

	private final String code;

	ActionType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
