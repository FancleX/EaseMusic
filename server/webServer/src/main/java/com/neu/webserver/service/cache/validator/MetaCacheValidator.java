package com.neu.webserver.service.cache.validator;

import com.neu.webserver.protocol.search.chain.ChainPackage;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;

public class MetaCacheValidator extends AbstractSearchHandlerChain implements CacheValidator {
    @Override
    protected boolean canHandle(ChainPackage chainPackage) {
        return false;
    }

    @Override
    public void handle(ChainPackage chainPackage) {

    }
}
