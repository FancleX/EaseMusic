package com.neu.webserver.config.search;

import com.neu.webserver.service.cache.CacheManager;
import com.neu.webserver.service.query.QueryManager;
import com.neu.webserver.service.search.SearchManager;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.SearchChainBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SearchConfig {

    private final QueryManager queryManager;
    private final CacheManager cacheManager;

    @Bean(name = "SearchManager")
    public SearchManager searchHandlerChain() {
        AbstractSearchHandlerChain chain = new SearchChainBuilder()
                .next(queryManager)
                .next(cacheManager)
                .build();

        return new SearchManager(chain);
    }

}
