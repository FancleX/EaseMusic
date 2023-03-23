package com.neu.webserver.config.download;

import com.neu.grpc.DownloadServiceGrpc;
import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.download.DownloadClientImpl;
import com.neu.webserver.service.download.DownloadManager;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
////@RequiredArgsConstructor
//@GrpcClientBean(
//    clazz = DownloadServiceGrpc.DownloadServiceStub.class,
//    beanName = "download-service-client",
//    client = @GrpcClient("grpc-download-service")
//)
public class DownloadConfig {


//    private final MediaRepository mediaRepository;

//    @Bean
//    public DownloadClientImpl downloadClient() {
//        return new DownloadClientImpl(mediaRepository);
//    }
//
//    @Bean
//    public DownloadManager downloadManager() {
//        return new DownloadManager(downloadClient(), mediaRepository);
//    }

}
