package com.neu.webserver.service.search;

import com.neu.webserver.protocol.media.MediaPreview;

import java.util.List;

public class SearchServiceImpl implements SearchService {
    @Override
    public List<MediaPreview> searchRawQuery(String query) {
        return null;
    }

    @Override
    public byte[] searchDetail(String uuid) {
        return new byte[0];
    }
}
