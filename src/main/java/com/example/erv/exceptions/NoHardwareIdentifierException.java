package com.example.erv.exceptions;

public class NoHardwareIdentifierException extends ErvException{
	private static final long serialVersionUID = 1L;

	public NoHardwareIdentifierException(String message) {
		super(message);
	}
}
