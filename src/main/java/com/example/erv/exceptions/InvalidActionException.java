package com.example.erv.exceptions;

import java.io.Serial;

public class InvalidActionException extends ErvException {
	@Serial
	private static final long serialVersionUID = 1L;
	private String localizationMessage;

	public InvalidActionException(String message) {
		super(message);
	}

	public InvalidActionException(String message, String localizationMessage) {
		super(message);
		this.localizationMessage = localizationMessage;
	}

	public String getLocalizationMessage() {
		return localizationMessage;
	}

}
