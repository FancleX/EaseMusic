package com.neu.webserver.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchManager searchManager;

    @Override
    public List<?> searchRawQuery(String query, int pageIndex) {
        return searchManager.doSearch(query, pageIndex);
    }

    @Override
    public byte[] searchDetail(String uuid) {
        return new byte[0];
    }
}
