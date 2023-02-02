package com.neu.webserver.config.search;

import com.neu.webserver.service.search.SearchManager;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.SearchChainBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class SearchConfig {

    private final AbstractSearchHandlerChain youTubeSearchFetcher;
    private final AbstractSearchHandlerChain metaCacheValidator;
    private final AbstractSearchHandlerChain metaCacheUpdater;

    @Bean()
    public SearchManager searchManager() {
        final AbstractSearchHandlerChain chain = new SearchChainBuilder()
                .next(metaCacheValidator)
                .next(youTubeSearchFetcher)
                .next(metaCacheUpdater)
                .build();

        return new SearchManager(chain);
    }
}
