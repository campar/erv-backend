package com.example.erv.exceptions;

import java.io.Serial;

public class CircularParentReferenceException extends ErvException {
	@Serial
    private static final long serialVersionUID = 1L;
	public static final String LOCALIZATION_KEY = "circular_parent_reference";

	public CircularParentReferenceException(String message) {
        super(message);
    }

}
