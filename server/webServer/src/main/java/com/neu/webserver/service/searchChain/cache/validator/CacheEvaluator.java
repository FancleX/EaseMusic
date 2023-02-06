package com.neu.webserver.service.searchChain.cache.validator;

import com.neu.webserver.protocol.media.MediaPreview;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CacheEvaluator {

    int getEntryAmount(@NonNull String queryString);

    List<MediaPreview> getFirstPageEntries(@NonNull String queryString);
}
