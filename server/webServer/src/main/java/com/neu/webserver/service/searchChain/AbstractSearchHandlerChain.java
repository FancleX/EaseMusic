package com.neu.webserver.service.searchChain;

import org.springframework.lang.NonNull;

public abstract class AbstractSearchHandlerChain {

    /**
     * The next handler in the chain.
     */
    protected AbstractSearchHandlerChain next;

    /**
     * Set the next handler of this handler.
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
    protected abstract boolean canHandle(@NonNull ChainPackage chainPackage);

    /**
     * Determine if next handler is available.
     *
     * @return true if this node has next handler, otherwise false
     */
    protected boolean hasNext() {
        return next != null;
    }

    /**
     * Handle and process the package.
     *
     * @param chainPackage the data package processed in the chain
     */
    public abstract void handle(ChainPackage chainPackage);

    /**
     * Determine if the current task is completed.
     *
     * @param chainPackage the data package processed in the chain
     * @return true if the package indicates complete status, otherwise false
     */
    protected boolean isCompleted(ChainPackage chainPackage) {
        return chainPackage.getNextStage().equals(ChainPackage.Status.COMPLETED);
    }

}
