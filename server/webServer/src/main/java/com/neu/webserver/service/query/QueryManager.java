package com.neu.webserver.service.query;

import com.neu.webserver.protocol.search.chain.ChainPackage;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;

import java.util.List;

// TODO: implement query manager and query service
public class QueryManager extends AbstractSearchHandlerChain {

    private final QueryService serviceProvider;

    public QueryManager(QueryService serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    protected boolean canHandle(ChainPackage chainPackage) {
        return false;
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        chainPackage.setQueryResult(List.of("a", "b", "c"));
        List<Object> aba = serviceProvider.query("aba");
        chainPackage.setQueryResult(aba);
        super.next.handle(chainPackage);
    }

}
