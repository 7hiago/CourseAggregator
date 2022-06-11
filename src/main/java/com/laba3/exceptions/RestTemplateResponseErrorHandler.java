package com.laba3.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response)
            throws IOException {
        try {
            return super.hasError(response);
        } catch (Exception e) {
            logger.error("Exception [" + e.getMessage() + "] occurred while trying to send the request", e);
            return true;
        }
    }

    @Override
    public void handleError(ClientHttpResponse response)
            throws IOException {
        try {
            super.handleError(response);
        } catch (Exception e) {
            logger.error("Exception [" + e.getMessage() + "] occurred while trying to send the request", e);
            throw e;
        }
    }
}
