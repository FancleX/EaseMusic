package com.neu.webserver.service.cache.updater;

import com.neu.webserver.protocol.search.chain.ChainPackage;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;

public class MetaCacheUpdater extends AbstractSearchHandlerChain implements CacheUpdater {
    @Override
    protected boolean canHandle(ChainPackage chainPackage) {
        return false;
    }

    @Override
    public void handle(ChainPackage chainPackage) {

    }
}
