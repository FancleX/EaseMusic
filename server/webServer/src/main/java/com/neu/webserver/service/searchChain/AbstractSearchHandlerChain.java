package com.neu.webserver.service.searchChain;

import com.neu.webserver.protocol.search.chain.ChainPackage;

public abstract class AbstractSearchHandlerChain {

    /**
     * The next handler in the chain.
     */
    protected AbstractSearchHandlerChain next;

    /**
     * Set the next handler after the handler.
     *
     * @param handler the next handler
     */
    public void setNext(AbstractSearchHandlerChain handler) {
        this.next = handler;
    }

    /**
     * Determine if current handler can handle the package.
     *
     * @param chainPackage the data package processed in the chain
     * @return true if the current handler is able to handle the package, otherwise false
     */
    protected abstract boolean canHandle(ChainPackage chainPackage);

    /**
     * Handle and process the package.
     *
     * @param chainPackage the data package processed in the chain
     */
    public abstract void handle(ChainPackage chainPackage);

}
