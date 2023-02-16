package com.neu.webserver.service.download;

import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

public interface DownloadClient {

    /**
     * Process download the given uuid file and stream to the given output stream.
     *
     * @param uuid         file uuid
     * @param outputStream output stream
     * @throws NoSuchAlgorithmException thrown if it is able to get algorithm for file digest
     * @throws InterruptedException     thrown if current download thread is interrupted on downloading
     */
    void download(String uuid, OutputStream outputStream) throws NoSuchAlgorithmException, InterruptedException;

    /**
     * Process read the given file with uuid, path, etc. Stream to the given output stream.
     *
     * @param uuid         file uuid
     * @param path         file path
     * @param start        start index
     * @param end          end index
     * @param outputStream output stream
     * @throws InterruptedException thrown if current read thread is interrupted on reading
     */
    void read(String uuid, String path, long start, long end, OutputStream outputStream) throws InterruptedException;
}
