package com.example.erv.exceptions;

import java.io.Serial;

public class OperationNotSupportedException extends ErvException {
	@Serial
	private static final long serialVersionUID = 1L;

	public OperationNotSupportedException(String message) {
		super(message);
	}
}
