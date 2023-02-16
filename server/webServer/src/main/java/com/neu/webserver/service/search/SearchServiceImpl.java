package com.neu.webserver.service.search;

import com.neu.webserver.entity.media.Media;
import com.neu.webserver.exception.search.DownloadInterruptException;
import com.neu.webserver.exception.search.DownloadTimeoutException;
import com.neu.webserver.exception.search.InvalidDownloadRequestParameterException;
import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.download.DownloadManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.concurrent.TimeoutException;

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
    public StreamingResponseBody searchDetail(String range, String uuid) {
        if (range == null || range.isBlank() || uuid == null || uuid.isBlank())
            throw new InvalidDownloadRequestParameterException("Unspecified download request parameters");

        Media media = mediaRepository.findByUuid(uuid);
        String audioPath = media.getAudioPath();
        long size = media.getSize();

        // initially download
        if (audioPath == null) {
            return outputStream -> {
                try {
                    downloadManager.submitDownloadTask(uuid, outputStream);
                } catch (InterruptedException e) {
                    throw new DownloadInterruptException();
                } catch (TimeoutException e) {
                    throw new DownloadTimeoutException(e.getMessage());
                }
            };
        }

        long start, end;
        try {
            String[] split = range.split("-");
            start = Long.parseLong(split[0]);
            end = split[1].isBlank() ? size : Long.parseLong(split[1]);
        } catch (NumberFormatException e) {
            throw new InvalidDownloadRequestParameterException(e.getMessage());
        }

        return outputStream -> {
            try {
                downloadManager.submitReadTask(uuid, audioPath, start, end, outputStream);
            } catch (InterruptedException e) {
                throw new DownloadInterruptException();
            }
        };
    }


}
