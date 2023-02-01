package com.neu.webserver.service.cache;

import com.neu.webserver.protocol.search.chain.ChainPackage;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;

import java.util.List;

public class CacheManager extends AbstractSearchHandlerChain {

    @Override
    protected boolean canHandle(ChainPackage chainPackage) {
        return false;
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        chainPackage.setQueryResult(List.of("abc", "efg"));
    }
}
