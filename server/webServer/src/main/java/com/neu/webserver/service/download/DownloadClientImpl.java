package com.neu.webserver.service.download;


import com.neu.grpc.*;
import com.neu.webserver.entity.media.Media;
import com.neu.webserver.repository.media.MediaRepository;
import io.grpc.stub.StreamObserver;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadClientImpl implements DownloadClient {

    private final MediaRepository mediaRepository;
    //    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    @GrpcClient("grpc-download-service")
    private DownloadServiceGrpc.DownloadServiceStub gRPCAsyncClient;

    @GrpcClient("grpc-download-service")
    private DownloadServiceGrpc.DownloadServiceBlockingStub gRPCBlockingClient;

    @Override
    public void download(String uuid, StringBuilder resultBuilder) throws NoSuchAlgorithmException, InterruptedException {
        DownloadRequest downloadRequest = DownloadRequest
                .newBuilder()
                .setUuid(uuid)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final FileDescription file = new FileDescription();
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        log.info("Start downloading file: " + uuid);
        gRPCAsyncClient.download(downloadRequest, new StreamObserver<>() {
            @Override
            public void onNext(DownloadResponse value) {
                if (file.filePath == null)
                    file.filePath = value.getFilePath();

                // calculate file size
                byte[] bytes = value.getFile().toByteArray();
                file.size += bytes.length;

                byteBuffer.writeBytes(bytes);
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error on downloading file: " + uuid + ", reason: " + t.getMessage());
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                byte[] fileBytes = byteBuffer.toByteArray();
                // hash
                final byte[] digest = md.digest(fileBytes);
                // save to database
                onSaveDownloadEvent(uuid, digest, file);
                // string encode
                String base64String = Base64.getEncoder().encodeToString(fileBytes);
                resultBuilder.append(base64String);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @Transactional
    protected void onSaveDownloadEvent(String uuid, final byte[] hash, FileDescription file) {
        // look up for a file with the same hash
        StringBuilder sb = new StringBuilder();
        for (byte h : hash)
            sb.append(String.format("%02x", h));

        final String hashString = sb.toString();

        final Media media = mediaRepository.findByUuid(uuid);

        media.setAudioPath(file.filePath);
        media.setHashCode(hashString);
        media.setSize(file.size);

        mediaRepository.save(media);

        log.info("Successfully download file: " + uuid + ", size: " + file.size);

//        mediaRepository
//                .findByHashCode(hashString)
//                .ifPresentOrElse(
//                        (existedFile -> {
//                            String audioPath = existedFile.getAudioPath();
//                            long size = existedFile.getSize();
//
//                            media.setAudioPath(audioPath);
//                            media.setSize(size);
//                            media.setHashCode(hashString);
//
//                            mediaRepository.save(media);
//
//                            log.info("Add a copy of the duplicated file: " + uuid + ", size: " + size);
//
//                            final DeleteResourceRequest deleteResourceRequest = DeleteResourceRequest.newBuilder().setFilePath(audioPath).build();
//                            executorService.submit(() -> gRPCBlockingClient.delete(deleteResourceRequest));
//                        }),
//                        () -> {
//
//                            media.setAudioPath(file.filePath);
//                            media.setHashCode(hashString);
//                            media.setSize(file.size);
//
//                            mediaRepository.save(media);
//
//                            log.info("Successfully download file: " + uuid + ", size: " + file.size);
//
//                        }
//                );
    }

    @Override
    public void read(String uuid, String path, long start, long end, StringBuilder resultBuilder) throws InterruptedException {
        final ReadFileRequest readFileRequest = ReadFileRequest
                .newBuilder()
                .setFilePath(path)
                .setStart(start)
                .setEnd(end)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ByteBuffer byteBuffer = ByteBuffer.allocate((int) (end - start + 1));

        log.info("Start reading file: " + uuid);
        gRPCAsyncClient.read(readFileRequest, new StreamObserver<>() {
            @Override
            public void onNext(ReadFileResponse value) {
                byteBuffer.put(value.getFile().asReadOnlyByteBuffer());
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error on reading file: " + uuid + ", reason: " + t.getMessage());
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("Successfully read file: " + uuid + " from " + start + " to " + end);
                String base64String = Base64.getEncoder().encodeToString(byteBuffer.array());
                resultBuilder.append(base64String);
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
