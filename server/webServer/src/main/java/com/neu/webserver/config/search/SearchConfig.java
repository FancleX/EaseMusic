package com.neu.webserver.config.search;

import com.neu.webserver.service.search.SearchManager;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.EndChainHandler;
import com.neu.webserver.service.searchChain.SearchChainBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@RequiredArgsConstructor
public class SearchConfig {

    private final AbstractSearchHandlerChain metaCacheEvaluator;
    private final AbstractSearchHandlerChain metaCacheUpdater;
    private final AbstractSearchHandlerChain youTubeSearchFetcher;

    @Bean
    public AbstractSearchHandlerChain endChainHandler() {
        return new EndChainHandler();
    }

    @Bean
    @Scope(value = "prototype")
    public AbstractSearchHandlerChain chain() {
        return new SearchChainBuilder()
                .next(metaCacheEvaluator)
                .next(youTubeSearchFetcher)
                .next(metaCacheUpdater)
                .next(endChainHandler())
                .build();
    }

    @Bean
    @Scope(value = "prototype")
    public SearchManager searchManager() {
        return new SearchManager(chain());
    }
}
