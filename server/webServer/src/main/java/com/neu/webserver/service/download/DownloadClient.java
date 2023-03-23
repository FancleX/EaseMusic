package com.neu.webserver.service.download;

import java.security.NoSuchAlgorithmException;

public interface DownloadClient {

    /**
     * Process download the given uuid file and stream to the given output stream.
     *
     * @param uuid          file uuid
     * @param resultBuilder base64 encoded file string
     * @throws NoSuchAlgorithmException thrown if it is able to get algorithm for file digest
     * @throws InterruptedException     thrown if current download thread is interrupted on downloading
     */
    void download(String uuid, StringBuilder resultBuilder) throws NoSuchAlgorithmException, InterruptedException;

    /**
     * Process read the given file with uuid, path, etc. Stream to the given output stream.
     *
     * @param uuid          file uuid
     * @param path          file path
     * @param start         start index
     * @param end           end index
     * @param resultBuilder base64 encoded file string
     * @throws InterruptedException thrown if current read thread is interrupted on reading
     */
    void read(String uuid, String path, long start, long end, StringBuilder resultBuilder) throws InterruptedException;
}
