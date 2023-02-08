package com.neu.webserver.service.searchChain.cache.validator;

import org.springframework.lang.NonNull;

import java.util.List;

public interface CacheEvaluator {

    int getEntryAmount(@NonNull String queryString);

    List<?> getEntriesByPage(@NonNull String queryString, int offset);
}
