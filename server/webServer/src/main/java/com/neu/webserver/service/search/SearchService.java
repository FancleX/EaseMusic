package com.neu.webserver.service.search;

import com.neu.webserver.protocol.search.response.SearchFileResponse;

import java.util.List;

public interface SearchService {

    /**
     * Take user query string to search a list of media meta.
     *
     * @param query     raw query string
     * @param pageIndex the page index
     * @return a list of media meta results
     */
    List<?> searchRawQuery(String query, int pageIndex);


    /**
     * TODO: Search for the specific audio source by given the uuid.
     *
     * @param range accept range of download related request
     * @param uuid  the uuid of the source
     * @return the stream of audio file
     */
    SearchFileResponse searchDetail(String range, String uuid);

}
