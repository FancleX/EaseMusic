package com.neu.webserver.service.search;

import com.neu.webserver.protocol.media.MediaPreview;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.ChainPackage;

import java.util.ArrayList;
import java.util.List;

public class SearchManager {

    private final AbstractSearchHandlerChain chain;

    public SearchManager(AbstractSearchHandlerChain chain) {
        this.chain = chain;
    }

    public List<?> doSearch(String rawInput) {
        final List<MediaPreview> mediaPreviews = new ArrayList<>();

        final ChainPackage chainPackage = ChainPackage
                .builder()
                .queryString(rawInput)
                .queryResult(mediaPreviews)
                .nextStage(ChainPackage.Status.CACHE_EVALUATE)
                .build();

        this.chain.handle(chainPackage);
        return mediaPreviews;
    }

}
