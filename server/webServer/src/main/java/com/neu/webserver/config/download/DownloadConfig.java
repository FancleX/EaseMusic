package com.neu.webserver.config.download;

import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.download.DownloadClientImpl;
import com.neu.webserver.service.download.DownloadManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DownloadConfig {

    private final MediaRepository mediaRepository;

    @Bean
    public DownloadClientImpl downloadClient() {
        return new DownloadClientImpl(mediaRepository);
    }

    @Bean
    public DownloadManager downloadManager() {
        return new DownloadManager(downloadClient(), mediaRepository);
    }

}
