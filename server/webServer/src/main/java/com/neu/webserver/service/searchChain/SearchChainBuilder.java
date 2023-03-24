package com.neu.webserver.service.searchChain;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

public class SearchChainBuilder {
    private final Queue<AbstractSearchHandlerChain> chainNodeList;

    public SearchChainBuilder() {
        this.chainNodeList = new ArrayDeque<>();
    }

    public SearchChainBuilder next(AbstractSearchHandlerChain chain) {
        chainNodeList.offer(chain);
        return this;
    }

    public AbstractSearchHandlerChain build() {
        if (chainNodeList.isEmpty())
            throw new NoSuchElementException("No handlers found");

        final AbstractSearchHandlerChain head = chainNodeList.poll();

        AbstractSearchHandlerChain current = head;

        while (!chainNodeList.isEmpty()) {
            AbstractSearchHandlerChain node = chainNodeList.poll();
            current.setNext(node);
            current = current.next;
        }

        return head;
    }

}
