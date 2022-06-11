package com.laba3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ApiInternalException extends RuntimeException {
    public ApiInternalException(String message) {
        super(message);
    }
}
