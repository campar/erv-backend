package com.example.erv.dataimport;

import com.example.erv.exceptions.ErvException;

public class UserImportException extends ErvException {
    public static final String LOCALIZATION_KEY = "user.upload_error" ;

    public UserImportException(String message) {
        super(message);
    }
}
