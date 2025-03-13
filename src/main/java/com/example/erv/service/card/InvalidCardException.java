package com.example.erv.service.card;

import com.example.erv.exceptions.ErvException;

public class InvalidCardException extends ErvException {
    public InvalidCardException(Object object) {
        super(object);
    }
}
