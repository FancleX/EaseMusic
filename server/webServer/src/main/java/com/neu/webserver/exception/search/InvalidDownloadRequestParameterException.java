package com.neu.webserver.exception.search;

public class InvalidDownloadRequestParameterException extends RuntimeException {
    public InvalidDownloadRequestParameterException(String message) {
        super(message);
    }
}
