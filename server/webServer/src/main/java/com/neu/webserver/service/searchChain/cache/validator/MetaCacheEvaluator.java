package com.neu.webserver.service.searchChain.cache.validator;

import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.ChainPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import java.util.List;

@RequiredArgsConstructor
public class MetaCacheEvaluator extends AbstractSearchHandlerChain implements CacheEvaluator {

    private final MediaRepository mediaRepository;
    @Value("${search.search-on-entries-below}")
    private int leastLimit;
    @Value("${search.entries-per-page}")
    private int entriesPerPage;

    @Override
    protected boolean canHandle(@NonNull ChainPackage chainPackage) {
        return chainPackage.getQueryString() != null
                && !chainPackage.getQueryString().isBlank()
                && ChainPackage.Status.CACHE_EVALUATE.equals(chainPackage.getNextStage());
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        if (!canHandle(chainPackage)) {
            super.next.handle(chainPackage);
            return;
        }

        final String queryString = chainPackage.getQueryString();
        int entryAmount = getEntryAmount(queryString);

        // don't have enough amount of query results
        if (entryAmount < leastLimit) {
            chainPackage.setNextStage(ChainPackage.Status.SEARCH);
            super.next.handle(chainPackage);
            return;
        }

        // has enough amount of query results
        List<?> entries = getEntriesByPage(queryString, chainPackage.getOffset());

        chainPackage.setQueryResult(entries);
        chainPackage.setNextStage(ChainPackage.Status.COMPLETED);
        super.next.handle(chainPackage);
    }

    @Override
    public int getEntryAmount(@NonNull String queryString) {
        return mediaRepository.getEntryAmount(queryString);
    }

    @Override
    public List<?> getEntriesByPage(@NonNull String queryString, int offset) {
        return mediaRepository.getMediaPreviewByPage(queryString, entriesPerPage, offset * entriesPerPage);
    }

}