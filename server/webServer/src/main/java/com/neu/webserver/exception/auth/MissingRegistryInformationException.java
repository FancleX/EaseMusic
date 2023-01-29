package com.neu.webserver.exception.auth;

public class MissingRegistryInformationException extends RuntimeException {
    public MissingRegistryInformationException(String message) {
        super(message);
    }
}
