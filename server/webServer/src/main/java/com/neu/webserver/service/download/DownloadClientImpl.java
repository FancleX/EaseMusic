package com.neu.webserver.service.download;


import com.neu.grpc.*;
import com.neu.webserver.entity.media.Media;
import com.neu.webserver.repository.media.MediaRepository;
import io.grpc.stub.StreamObserver;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class DownloadClientImpl implements DownloadClient {

    private final MediaRepository mediaRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    @GrpcClient("grpc-download-service")
    private DownloadServiceGrpc.DownloadServiceStub gRPCAsyncClient;

    @Override
    public void download(String uuid, OutputStream outputStream) throws NoSuchAlgorithmException, InterruptedException {
        DownloadRequest downloadRequest = DownloadRequest
                .newBuilder()
                .setUuid(uuid)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final FileDescription file = new FileDescription();
        final MessageDigest md = MessageDigest.getInstance("SHA-256");

        log.info("Start downloading file: " + uuid);
        gRPCAsyncClient.download(downloadRequest, new StreamObserver<>() {
            @Override
            public void onNext(DownloadResponse value) {
                file.filePath = value.getFilePath();

                try (outputStream) {
                    // calculate hash and file size
                    byte[] bytes = value.getFile().toByteArray();
                    md.update(bytes);
                    file.size += bytes.length;

                    // stream to response
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error on downloading file: " + uuid + ", reason: " + t.getMessage());
                onSaveDownloadEvent(uuid, md, file);
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                onSaveDownloadEvent(uuid, md, file);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @Transactional
    protected void onSaveDownloadEvent(String uuid, MessageDigest md, FileDescription file) {
        // look up for a file with the same hash
        final String hash = new String(md.digest());
        final Media media = mediaRepository.findByUuid(uuid);

        mediaRepository
                .findByHashCode(hash)
                .ifPresentOrElse(
                        (existedFile -> {
                            String audioPath = existedFile.getAudioPath();
                            long size = existedFile.getSize();

                            media.setAudioPath(audioPath);
                            media.setSize(size);
                            media.setHashCode(hash);

                            mediaRepository.save(media);

                            log.info("Add a copy of the duplicated file: " + uuid + ", size: " + size);

                            final DeleteResourceRequest deleteResourceRequest = DeleteResourceRequest.newBuilder().setFilePath(audioPath).build();
                            executorService.submit(() -> gRPCAsyncClient.delete(deleteResourceRequest, null));
                        }),
                        () -> {

                            media.setAudioPath(file.filePath);
                            media.setHashCode(hash);
                            media.setSize(file.size);

                            mediaRepository.save(media);

                            log.info("Successfully download file: " + uuid + ", size: " + file.size);
                        }
                );
    }

    @Override
    public void read(String uuid, String path, long start, long end, OutputStream outputStream) throws InterruptedException {
        final ReadFileRequest readFileRequest = ReadFileRequest
                .newBuilder()
                .setFilePath(path)
                .setStart(start)
                .setEnd(end)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        log.info("Start reading file: " + uuid);
        gRPCAsyncClient.read(readFileRequest, new StreamObserver<>() {
            @Override
            public void onNext(ReadFileResponse value) {
                try (outputStream) {
                    outputStream.write(value.getFile().toByteArray());
                    outputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error on reading file: " + uuid + ", reason: " + t.getMessage());
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("Successfully read file: " + uuid);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @NoArgsConstructor
    static class FileDescription {
        String filePath;
        long size;
    }
}
