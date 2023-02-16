package com.neu.webserver.exception.search;

public class DownloadTimeoutException extends RuntimeException {
    public DownloadTimeoutException(String message) {
        super(message);
    }
}
