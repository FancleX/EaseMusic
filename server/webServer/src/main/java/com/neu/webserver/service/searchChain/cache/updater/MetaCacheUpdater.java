package com.neu.webserver.service.searchChain.cache.updater;

import com.neu.webserver.entity.media.Media;
import com.neu.webserver.protocol.media.MediaPreview;
import com.neu.webserver.repository.media.MediaRepository;
import com.neu.webserver.service.searchChain.AbstractSearchHandlerChain;
import com.neu.webserver.service.searchChain.ChainPackage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class MetaCacheUpdater extends AbstractSearchHandlerChain implements CacheUpdater {

    private final MediaRepository mediaRepository;
    private final EntityManager entityManager;
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
        List<?> result = mediaRepository.getMediaPreviewByPage(chainPackage.getQueryString(), entriesPerPage, chainPackage.getOffset() * entriesPerPage);

        chainPackage.setQueryResult(result);
        chainPackage.setNextStage(ChainPackage.Status.COMPLETED);

        super.next.handle(chainPackage);
    }

    @Override
    @Transactional
    public void saveInstances(String topic, List<?> mediaList) {
        List<String> uuids = mediaList
                .stream()
                .map((media) -> {
                    MediaPreview preview = (MediaPreview) media;
                    return preview.getUuid();
                })
                .toList();

        String jpql = """
                    SELECT media FROM Media media
                    WHERE media.uuid IN :uuids
                """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("uuids", uuids);

        List<Media> existingEntities = query.getResultList();
        existingEntities.forEach(e -> e.getRelatedTopics().add(topic));

        Map<String, Media> existingEntityMap = existingEntities.stream().collect(Collectors.toMap(Media::getUuid, Function.identity()));

        List<Media> instancesToBeInserted = mediaList
                .stream()
                .map((media) -> {
                    MediaPreview preview = (MediaPreview) media;

                    Media m = existingEntityMap
                            .getOrDefault(
                                    preview.getUuid(),
                                    Media
                                            .builder()
                                            .uuid(preview.getUuid())
                                            .author(preview.getAuthor())
                                            .thumbnail(preview.getThumbnail())
                                            .title(preview.getTitle())
                                            .description(preview.getDescription())
                                            .relatedTopics(new HashSet<>())
                                            .build()
                            );
                    m.getRelatedTopics().add(topic);

                    return m;
                })
                .toList();

        mediaRepository.saveAll(instancesToBeInserted);
    }
}
