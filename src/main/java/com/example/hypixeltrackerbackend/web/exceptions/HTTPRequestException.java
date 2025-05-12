package com.example.hypixeltrackerbackend.web.exceptions;

import org.springframework.http.HttpStatus;

public class HTTPRequestException extends Exception {
    private final HttpStatus statusCode;

    public HTTPRequestException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
