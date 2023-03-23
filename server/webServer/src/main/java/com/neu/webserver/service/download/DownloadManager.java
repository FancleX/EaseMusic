package com.neu.webserver.service.download;

import com.neu.webserver.exception.search.DownloadTimeoutException;
import com.neu.webserver.exception.search.UnableStartDownloadingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
@Slf4j
public class DownloadManager {
    private final ConcurrentHashMap<String, Lock> taskPool = new ConcurrentHashMap<>();
    private final DownloadClient downloadClient;

    public void submitDownloadTask(String uuid, StringBuilder resultBuilder) throws InterruptedException, DownloadTimeoutException {
        Lock lock = taskPool.putIfAbsent(uuid, new ReentrantLock());
        if (lock == null)
            lock = taskPool.get(uuid);

        if (lock.tryLock(30, TimeUnit.SECONDS)) {
            try {
                downloadClient.download(uuid, resultBuilder);
            } catch (NoSuchAlgorithmException e) {
                throw new UnableStartDownloadingException(e.getMessage());
            } finally {
                lock.unlock();
                taskPool.remove(uuid);
            }
        } else {
            log.error("Download file: " + uuid + " timed out");
            throw new DownloadTimeoutException("Download request timeout");
        }
    }


    public void submitReadTask(String uuid, String path, long start, long end, StringBuilder resultBuilder) throws InterruptedException {
        downloadClient.read(uuid, path, start, end, resultBuilder);
    }

}
