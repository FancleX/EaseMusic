package com.neu.webserver.config.cache;

import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.searchChain.cache.updater.CacheUpdater;
import com.neu.webserver.service.searchChain.cache.updater.MetaCacheUpdater;
import com.neu.webserver.service.searchChain.cache.validator.CacheEvaluator;
import com.neu.webserver.service.searchChain.cache.validator.MetaCacheEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CacheManagerConfig {

    private final MediaRepository mediaRepository;

    @Bean
    public CacheUpdater metaCacheUpdater() {
        return new MetaCacheUpdater();
    }


    @Bean
    public CacheEvaluator metaCacheEvaluator() {
        return new MetaCacheEvaluator(mediaRepository);
    }
}
