package com.neu.webserver.service.download;

import com.neu.webserver.exception.search.UnableStartDownloadingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
@Service
public class DownloadManager {
    private final DownloadClient downloadClient;

    public void submitDownloadTask(String uuid, StringBuilder resultBuilder) throws InterruptedException {
        try {
            downloadClient.download(uuid, resultBuilder);
        } catch (NoSuchAlgorithmException e) {
            throw new UnableStartDownloadingException(e.getMessage());
        }
    }


    public void submitReadTask(String uuid, String path, long start, long end, StringBuilder resultBuilder) throws InterruptedException {
        downloadClient.read(uuid, path, start, end, resultBuilder);
    }

}
