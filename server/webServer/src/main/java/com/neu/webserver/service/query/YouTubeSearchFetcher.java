package com.neu.webserver.service.query;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.neu.webserver.exception.search.NoHandlerHandlesChainPackageException;
import com.neu.webserver.exception.search.UnableConnectYouTubeServiceException;
import com.neu.webserver.protocol.media.MediaPreview;
import com.neu.webserver.protocol.search.chain.ChainPackage;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YouTubeSearchFetcher extends AbstractSearchHandlerChain implements QueryService {

    private final YouTube youtubeService;
    @Value("${YouTube.apiKey}")
    private String DEVELOPER_KEY;

    @Override
    protected boolean canHandle(ChainPackage chainPackage) {
        return chainPackage != null
                && chainPackage.getQueryString() != null
                && !chainPackage.getQueryString().isBlank()
                && chainPackage.getQueryResult() == null;
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        if (!canHandle(chainPackage)) {
            if (hasNext()) {
                super.next.handle(chainPackage);
                return;
            }
            throw new NoHandlerHandlesChainPackageException("Chain package reaches the end of search chain," +
                    " and no handler found to handle it");
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
                    .setChannelType("any")
                    .setMaxResults(50L)
                    .setOrder("relevance")
                    .setQ("software")
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
