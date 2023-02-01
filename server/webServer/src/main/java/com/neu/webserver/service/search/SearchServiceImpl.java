package com.neu.webserver.service.search;

import com.neu.webserver.protocol.media.MediaPreview;
import com.neu.webserver.protocol.search.chain.ChainPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final SearchManager searchManager;

    @Autowired
    public SearchServiceImpl(@Qualifier(value = "SearchManager") SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    @Override
    public List<MediaPreview> searchRawQuery(String query) {
        ChainPackage chainPackage = ChainPackage
                .builder()
                .queryString("abac")
                .build();

        List<?> results = searchManager.doSearch(chainPackage);
        System.out.println(results);
        return null;
    }

    @Override
    public byte[] searchDetail(String uuid) {
        return new byte[0];
    }
}
