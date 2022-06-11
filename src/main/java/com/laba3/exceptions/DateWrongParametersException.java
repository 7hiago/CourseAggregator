package com.laba3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DateWrongParametersException extends RuntimeException {
    public DateWrongParametersException(String message) {
        super(message);
    }
}
