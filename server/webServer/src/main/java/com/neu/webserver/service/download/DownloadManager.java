package com.neu.webserver.service.download;

import com.neu.webserver.entity.media.Media;
import com.neu.webserver.exception.search.UnableStartDownloadingException;
import com.neu.webserver.repository.media.MediaRepository;
import lombok.RequiredArgsConstructor;

import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class DownloadManager {

    private final Map<String, ReentrantLock> taskMap = new ConcurrentHashMap<>();
    private final DownloadClient downloadClient;
    private final MediaRepository mediaRepository;

    public void submitDownloadTask(String uuid, OutputStream outputStream) throws TimeoutException, InterruptedException {
        // get a lock of current uuid download task
        ReentrantLock lock = taskMap.computeIfAbsent(uuid, k -> new ReentrantLock());
        // lock when processing download
        boolean acquireLock = lock.tryLock(2, TimeUnit.MINUTES);
        // waiting timeout
        if (!acquireLock) throw new TimeoutException();

        try {
            if (!taskMap.containsKey(uuid)) {
                // initial download
                downloadClient.download(uuid, outputStream);
                taskMap.remove(uuid);
            } else {
                // resource already downloaded, dispatch to read
                Media media = mediaRepository.findByUuid(uuid);
                submitReadTask(uuid, media.getAudioPath(), 0, media.getSize(), outputStream);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new UnableStartDownloadingException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }


    public void submitReadTask(String uuid, String path, long start, long end, OutputStream outputStream) throws InterruptedException {
        downloadClient.read(uuid, path, start, end, outputStream);
    }

}
