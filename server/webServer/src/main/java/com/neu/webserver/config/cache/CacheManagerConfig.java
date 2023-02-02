package com.neu.webserver.config.cache;

import com.neu.webserver.service.cache.updater.CacheUpdater;
import com.neu.webserver.service.cache.updater.MediaCacheUpdater;
import com.neu.webserver.service.cache.updater.MetaCacheUpdater;
import com.neu.webserver.service.cache.validator.CacheValidator;
import com.neu.webserver.service.cache.validator.MediaCacheValidator;
import com.neu.webserver.service.cache.validator.MetaCacheValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheManagerConfig {

    @Bean
    public CacheUpdater metaCacheUpdater() {
        return new MetaCacheUpdater();
    }

    @Bean
    public CacheUpdater mediaCacheUpdater() {
        return new MediaCacheUpdater();
    }

    @Bean
    public CacheValidator metaCacheValidator() {
        return new MetaCacheValidator();
    }

    @Bean
    public CacheValidator mediaCacheValidator() {
        return new MediaCacheValidator();
    }
}
