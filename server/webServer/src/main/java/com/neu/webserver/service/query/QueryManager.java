package com.neu.webserver.service.query;

import com.neu.webserver.protocol.search.chain.ChainPackage;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;

import java.util.List;


public class QueryManager extends AbstractSearchHandlerChain {

    @Override
    protected boolean canHandle(ChainPackage chainPackage) {
        return false;
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        chainPackage.setQueryResult(List.of("a", "b", "c"));
        super.next.handle(chainPackage);
    }
}
