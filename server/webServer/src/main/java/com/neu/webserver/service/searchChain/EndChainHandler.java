package com.neu.webserver.service.searchChain;

import com.neu.webserver.exception.search.NoHandlerHandlesChainPackageException;
import lombok.NonNull;

public class EndChainHandler extends AbstractSearchHandlerChain {
    @Override
    protected boolean canHandle(@NonNull ChainPackage chainPackage) {
        return true;
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        if (canHandle(chainPackage) && isCompleted(chainPackage)) return;

        // has no next handler and task is uncompleted
        throw new NoHandlerHandlesChainPackageException("Chain package reaches the end of search chain," +
                " and no handler found to handle it");
    }
}
