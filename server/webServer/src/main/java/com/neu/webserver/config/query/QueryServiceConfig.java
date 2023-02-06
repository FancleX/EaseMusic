package com.neu.webserver.config.query;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.neu.webserver.service.searchChain.query.YouTubeSearchFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
@RequiredArgsConstructor
public class QueryServiceConfig {

    private static final GsonFactory GSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("${YouTube.applicationName}")
    private String APPLICATION_NAME;

    @Bean
    public YouTube getService() {
        final NetHttpTransport httpTransport;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return new YouTube.Builder(httpTransport, GSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Bean
    public YouTubeSearchFetcher youTubeSearchFetcher() {
        return new YouTubeSearchFetcher(getService());
    }
}
