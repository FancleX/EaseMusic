package com.neu.webserver.config.cache;

import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.cache.updater.MetaCacheUpdater;
import com.neu.webserver.service.searchChain.cache.validator.MetaCacheEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CacheManagerConfig {

    private final MediaRepository mediaRepository;

    @Bean(name = "metaCacheUpdater")
    public AbstractSearchHandlerChain metaCacheUpdater() {
        return new MetaCacheUpdater(mediaRepository);
    }

    @Bean(name = "metaCacheEvaluator")
    public AbstractSearchHandlerChain metaCacheEvaluator() {
        return new MetaCacheEvaluator(mediaRepository);
    }
}
