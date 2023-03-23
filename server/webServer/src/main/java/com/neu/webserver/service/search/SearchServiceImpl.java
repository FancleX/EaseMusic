package com.neu.webserver.service.search;

import com.neu.webserver.entity.media.Media;
import com.neu.webserver.exception.search.DownloadInterruptedException;
import com.neu.webserver.exception.search.InvalidDownloadRequestParameterException;
import com.neu.webserver.protocol.search.response.SearchFileResponse;
import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.download.DownloadManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchManager searchManager;
    private final DownloadManager downloadManager;
    private final MediaRepository mediaRepository;

    @Override
    public List<?> searchRawQuery(String query, int pageIndex) {
        return searchManager.doSearch(query, pageIndex);
    }

    @Override
    public SearchFileResponse searchDetail(String range, String uuid) {
        if (range == null || range.isBlank() || uuid == null || uuid.isBlank())
            throw new InvalidDownloadRequestParameterException("Unspecified download request parameters");

        Media media = mediaRepository.findByUuid(uuid);
        String audioPath = media.getAudioPath();
        long size = media.getSize();

        // initially download
        if (audioPath == null || audioPath.isBlank()) {
            try {
                final StringBuilder resultBuilder = new StringBuilder();
                downloadManager.submitDownloadTask(uuid, resultBuilder);

                return new SearchFileResponse(resultBuilder.toString());
            } catch (InterruptedException e) {
                throw new DownloadInterruptedException();
            }
        }

        long start, end;
        try {
            String[] split = range.split("-");
            start = Long.parseLong(split[0].trim());
            end = split.length > 1 ? Long.parseLong(split[1].trim()) : size;
        } catch (NumberFormatException e) {
            throw new InvalidDownloadRequestParameterException(e.getMessage());
        }

        try {
            final StringBuilder resultBuilder = new StringBuilder();
            downloadManager.submitReadTask(uuid, audioPath, start, end, resultBuilder);

            return new SearchFileResponse(resultBuilder.toString());
        } catch (InterruptedException e) {
            throw new DownloadInterruptedException();
        }
    }
}
