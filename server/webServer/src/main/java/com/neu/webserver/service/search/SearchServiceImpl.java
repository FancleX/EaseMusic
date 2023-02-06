package com.neu.webserver.service.search;

import com.neu.webserver.protocol.media.MediaPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchManager searchManager;

    @Override
    public List<MediaPreview> searchRawQuery(String query) {

        List<?> results = searchManager.doSearch(query);
        System.out.println(results);
        return null;
    }

    @Override
    public byte[] searchDetail(String uuid) {
        return new byte[0];
    }
}
