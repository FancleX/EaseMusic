package com.neu.webserver.exception.search;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DownloadInterruptedException extends RuntimeException {


    public DownloadInterruptedException(String message) {
        super(message);
    }
}
