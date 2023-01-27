package com.neu.webserver.exception.auth;

public class MissingRegisterInformationException extends RuntimeException {
    public MissingRegisterInformationException(String message) {
        super(message);
    }
}
