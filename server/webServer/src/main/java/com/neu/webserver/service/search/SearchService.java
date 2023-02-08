package com.neu.webserver.service.search;

import com.neu.webserver.protocol.media.MediaPreview;

import java.util.List;

public interface SearchService {

    /**
     * TODO: Take user query string to search a list of media meta.
     *
     * @param query     raw query string
     * @param pageIndex the page index
     * @return a list of media meta results
     */
    List<MediaPreview> searchRawQuery(String query, int pageIndex);

    /**
     * TODO: Search for the specific audio source by given the uuid.
     *
     * @param uuid the uuid of the source
     * @return the stream of audio file
     */
    byte[] searchDetail(String uuid);

}
