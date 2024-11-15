package com.example.erv.exceptions;

import java.io.Serial;

public class DeletedUserException extends ErvException {
	@Serial
    private static final long serialVersionUID = 1L;
	public static final String LOCALIZATION_KEY = "terminated_account";

	public DeletedUserException(String message) {
        super(message);
    }


}
