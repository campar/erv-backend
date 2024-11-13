package com.example.erv.dto;

import java.util.Map;

import com.example.erv.exceptions.LocalizationKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

	private String code;
	private String message;
	private Map<String, Object> details;
	private String localizationKey;

	public ErrorResponse(String message, Map<String, Object> details) {
		super();
		this.message = message;
		this.details = details;
	}

	public ErrorResponse(String code, String message, Map<String, Object> details) {
		super();
		this.code = code;
		this.message = message;
		this.details = details;
	}

	public ErrorResponse(String code, String message, Map<String, Object> details, LocalizationKey localizationKey) {
		super();
		this.code = code;
		this.message = message;
		this.details = details;
		this.localizationKey = localizationKey.getKey();
	}
	public ErrorResponse(String code, String message, LocalizationKey localizationKey) {
		super();
		this.code = code;
		this.message = message;
		this.localizationKey = localizationKey.getKey();
	}

}
