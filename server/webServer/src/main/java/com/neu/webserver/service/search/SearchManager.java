package com.neu.webserver.service.search;

import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.ChainPackage;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SearchManager {
    private final AbstractSearchHandlerChain chain;

    public List<?> doSearch(String rawInput, int pageIndex) {

        final ChainPackage chainPackage = ChainPackage
                .builder()
                .queryString(rawInput)
                .offset(pageIndex)
                .nextStage(ChainPackage.Status.CACHE_EVALUATE)
                .build();

        this.chain.handle(chainPackage);

        return chainPackage.getQueryResult();
    }

}
