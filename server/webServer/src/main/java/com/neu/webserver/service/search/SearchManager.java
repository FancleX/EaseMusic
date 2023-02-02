package com.neu.webserver.service.search;

import com.neu.webserver.protocol.search.chain.ChainPackage;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;

import java.util.List;

public class SearchManager {

    private final AbstractSearchHandlerChain chain;

    public SearchManager(AbstractSearchHandlerChain chain) {
        this.chain = chain;
    }

    public List<?> doSearch(String rawInput) {
        ChainPackage chainPackage = ChainPackage
                .builder()
                .queryString(rawInput)
                .build();

        this.chain.handle(chainPackage);
        return chainPackage.getQueryResult();
    }

}
