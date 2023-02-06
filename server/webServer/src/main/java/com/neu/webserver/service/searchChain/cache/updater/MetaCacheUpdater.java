package com.neu.webserver.service.searchChain.cache.updater;

import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.ChainPackage;
import org.springframework.lang.NonNull;

public class MetaCacheUpdater extends AbstractSearchHandlerChain implements CacheUpdater {
    @Override
    protected boolean canHandle(@NonNull ChainPackage chainPackage) {
        return false;
    }

    @Override
    public void handle(ChainPackage chainPackage) {

    }
}
