package com.neu.webserver.config.cache;

import com.neu.webserver.service.cache.CacheManager;
import com.neu.webserver.service.cache.CacheService;
import com.neu.webserver.service.cache.MetaCacher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheManagerConfig {

    @Bean
    public CacheService cacheServiceProvider() {
        return new MetaCacher();
    }

    @Bean
    public CacheManager cacheManager() {
        return new CacheManager(cacheServiceProvider());
    }

}
