package com.neu.webserver.service.searchChain.query;

import com.neu.webserver.protocol.media.MediaPreview;

import java.util.List;

public interface QueryService {

    /**
     * Query the given string input and compose the search results.
     *
     * @param rawInput raw string input
     * @return a list of result media preview
     */
    List<? extends MediaPreview> doQuery(String rawInput);
}
