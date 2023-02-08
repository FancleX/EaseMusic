package com.neu.webserver.controller.search;

import com.neu.webserver.protocol.media.MediaPreview;
import com.neu.webserver.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    // search raw user input
    @GetMapping("/results")
    public ResponseEntity<List<MediaPreview>> searchRawQuery(@RequestParam("search_query") String query, @RequestParam("page_index") int pageIndex) {
        return ResponseEntity.ok(searchService.searchRawQuery(query, pageIndex));
    }

    // internal search a set of uuids


    // search a specific uuid
    // byte stream of the audio file
    @GetMapping("/detail")
    public ResponseEntity<byte[]> searchDetail(@RequestParam("uuid") String uuid) {
        return ResponseEntity.ok(searchService.searchDetail(uuid));
    }
}
