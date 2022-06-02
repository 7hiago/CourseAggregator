package com.laba3.CourseAggregator.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public AppResponseEntityExceptionHandler() {
        super();
    }

    // 400

    @ExceptionHandler(value = { DateWrongParametersException.class })
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        final String bodyOfResponse = "Bad request! " + ex.getMessage();
        logger.debug("400 Status Code. " + ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ BadRequestException.class })
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, WebRequest request) {
        final String bodyOfResponse = "Bad request! " + ex.getMessage();
        logger.debug("400 Status Code. " + ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // 404

    @ExceptionHandler(value = { CourseNotFoundException.class, ResourceNotFoundException.class })
    public ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        final String bodyOfResponse = "Not found! " + ex.getMessage();
        logger.debug("404 Status Code. " + ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    // 500

    @ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class, ApiInternalException.class })
    public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
        final String bodyOfResponse = "Internal server error! " + ex.getMessage();
        logger.error("500 Status Code. " + ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}