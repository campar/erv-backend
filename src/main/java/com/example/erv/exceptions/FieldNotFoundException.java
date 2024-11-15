package com.example.erv.exceptions;

import java.io.Serial;

public class FieldNotFoundException extends ErvException {
	@Serial
    private static final long serialVersionUID = 1L;

	public FieldNotFoundException(String message) {
        super(message);
    }
}
