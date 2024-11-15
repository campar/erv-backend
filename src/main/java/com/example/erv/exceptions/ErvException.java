package com.example.erv.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serial;

public class ErvException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ErvException(String message) {
        super(message);
    }

    public ErvException(String... message) {
        super(String.join(" : ", message));
    }

    public ErvException(Object object) {
        super(parseObject(object));
    }

    private static String parseObject(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return object.toString();
        }
    }
}
