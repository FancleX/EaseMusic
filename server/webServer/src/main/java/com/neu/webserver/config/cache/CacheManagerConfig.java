package com.neu.webserver.config.cache;

import com.neu.webserver.service.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheManagerConfig {

    @Bean
    public CacheManager cacheManager() {
        return new CacheManager();
    }
}
