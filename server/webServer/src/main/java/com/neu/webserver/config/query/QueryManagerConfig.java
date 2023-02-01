package com.neu.webserver.config.query;

import com.neu.webserver.service.query.QueryManager;
import com.neu.webserver.service.query.QueryService;
import com.neu.webserver.service.query.QueryServiceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryManagerConfig {

    @Bean
    public QueryService queryService() {
        return new QueryServiceProvider();
    }

    @Bean
    public QueryManager queryManager() {
        return new QueryManager(queryService());
    }
}
