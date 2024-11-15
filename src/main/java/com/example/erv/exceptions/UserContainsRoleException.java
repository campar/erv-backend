package com.example.erv.exceptions;

import java.io.Serial;

public class UserContainsRoleException extends ErvException {
	@Serial
    private static final long serialVersionUID = 1L;
	public static final String LOCALIZATION_KEY = "users_contains_role";

	public UserContainsRoleException(String message) {
        super(message);
    }

}
