package com.neu.webserver.config.query;

import com.neu.webserver.service.query.QueryManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryManagerConfig {

    @Bean
    public QueryManager queryManager() {
        return new QueryManager();
    }
}
