package com.neu.webserver.service.searchChain.query;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.neu.webserver.exception.search.UnableConnectYouTubeServiceException;
import com.neu.webserver.protocol.media.MediaPreview;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.ChainPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class YouTubeSearchFetcher extends AbstractSearchHandlerChain implements QueryService {

    private final YouTube youtubeService;
    @Value("${YouTube.apiKey}")
    private String DEVELOPER_KEY;

    @Override
    protected boolean canHandle(@NonNull ChainPackage chainPackage) {
        return chainPackage.getQueryString() != null
                && !chainPackage.getQueryString().isBlank();
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        if (!canHandle(chainPackage)) {
            if (hasNext()) {
                super.next.handle(chainPackage);
                return;
            }
        }

        List<MediaPreview> queryResults = doQuery(chainPackage.getQueryString());
        chainPackage.setQueryResult(queryResults);

        if (hasNext())
            super.next.handle(chainPackage);
    }

    @Override
    public List<MediaPreview> doQuery(String rawInput) {
        // Define and execute the API request
        try {
            YouTube.Search.List request = youtubeService
                    .search()
                    .list(Collections.singletonList("snippet"));

            SearchListResponse searchListResponse = request
                    .setKey(DEVELOPER_KEY)
                    .setType(Collections.singletonList("video"))
                    .setChannelType("any")
                    .setMaxResults(50L)
                    .setOrder("relevance")
                    .setQ(rawInput)
                    .setType(Collections.singletonList("video"))
                    .setVideoCaption("any")
                    .setVideoDefinition("any")
                    .setVideoDimension("2d")
                    .setVideoDuration("any")
                    .setFields("items(id(videoId),snippet(title,description,thumbnails(medium(url)),channelTitle))")
                    .execute();

            return searchListResponse
                    .getItems()
                    .stream()
                    .map((searchResult -> {
                        ResourceId id = searchResult.getId();
                        SearchResultSnippet snippet = searchResult.getSnippet();

                        return MediaPreview
                                .builder()
                                .uuid(id.getVideoId())
                                .title(snippet.getTitle())
                                .description(snippet.getDescription())
                                .thumbnail(snippet.getThumbnails().getMedium().getUrl())
                                .author(snippet.getChannelTitle())
                                .build();
                    }))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UnableConnectYouTubeServiceException("Unable to send request to YouTube API");
        }
    }
}
