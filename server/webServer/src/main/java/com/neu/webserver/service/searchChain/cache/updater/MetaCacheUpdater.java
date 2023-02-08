package com.neu.webserver.service.searchChain.cache.updater;

import com.neu.webserver.entity.media.Media;
import com.neu.webserver.protocol.media.MediaPreview;
import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.ChainPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class MetaCacheUpdater extends AbstractSearchHandlerChain implements CacheUpdater {

    private final MediaRepository mediaRepository;

    @Value("${search.entries-per-page}")
    private int entriesPerPage;

    @Override
    protected boolean canHandle(@NonNull ChainPackage chainPackage) {
        return chainPackage.getQueryString() != null
                && !chainPackage.getQueryString().isBlank()
                && ChainPackage.Status.CACHE_UPDATE.equals(chainPackage.getNextStage());
    }

    @Override
    public void handle(ChainPackage chainPackage) {
        if (!canHandle(chainPackage)) {
            super.next.handle(chainPackage);
            return;
        }

        // get recent fetch from YouTube, cache them in database
        List<?> queryResult = chainPackage.getQueryResult();

        saveInstances(chainPackage.getQueryString(), queryResult);
        List<?> result = mediaRepository.getMediaPreviewByPage(chainPackage.getQueryString(), entriesPerPage, chainPackage.getOffset());

        chainPackage.setQueryResult(result);
        chainPackage.setNextStage(ChainPackage.Status.COMPLETED);

        super.next.handle(chainPackage);
    }

    @Override
    @Transactional
    public void saveInstances(String topic, List<?> mediaList) {
        mediaList
                .forEach(mediaPreview -> {
                            MediaPreview preview = (MediaPreview) mediaPreview;

                            mediaRepository
                                    .findByUuid(preview.getUuid())
                                    .ifPresentOrElse(
                                            media -> {
                                                assert media.getRelatedTopics() != null;
                                                media.getRelatedTopics().add(topic);
                                                mediaRepository.save(media);
                                            },
                                            () -> {
                                                Set<String> topicsSet = new HashSet<>();
                                                topicsSet.add(topic);

                                                Media media = Media
                                                        .builder()
                                                        .title(preview.getTitle())
                                                        .uuid(preview.getUuid())
                                                        .author(preview.getAuthor())
                                                        .thumbnail(preview.getThumbnail())
                                                        .description(preview.getDescription())
                                                        .relatedTopics(topicsSet)
                                                        .build();

                                                mediaRepository.save(media);
                                            }
                                    );
                        }
                );
    }
}
