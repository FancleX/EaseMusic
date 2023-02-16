package com.neu.webserver.controller.search;

import com.neu.webserver.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    // search raw user input
    @GetMapping("/results")
    public ResponseEntity<List<?>> searchRawQuery(@RequestParam("search_query") String query, @RequestParam("page_index") int pageIndex) {
        return ResponseEntity.ok(searchService.searchRawQuery(query, pageIndex));
    }

    // search a specific uuid
    // byte stream of the audio file
    @GetMapping("/detail")
    public ResponseEntity<StreamingResponseBody> searchDetail(@RequestHeader(HttpHeaders.ACCEPT_RANGES) String range, @RequestParam("uuid") String uuid) {

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.builder("attachment").filename(uuid + ".mp3").build().toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(searchService.searchDetail(range, uuid));
    }
}
