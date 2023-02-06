package com.neu.webserver.repository.media;

import com.neu.webserver.entity.media.Media;
import com.neu.webserver.protocol.media.MediaPreview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query(value =
            """
                        SELECT COUNT(*)
                        FROM media
                        INNER JOIN media_related_topics
                            ON media.id = media_related_topics.media_id
                            AND related_topics LIKE CONCAT('%',:queryString,'%')
                    """,
            nativeQuery = true)
    int getEntryAmount(@Param("queryString") String queryString);

    @Query(value = """
                SELECT author, description, thumbnail, title, uuid
                FROM media
                INNER JOIN media_related_topics
                    ON media.id = media_related_topics.media_id
                    AND related_topics LIKE CONCAT('%',:queryString,'%')
                LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MediaPreview> getMediaPreviewByPage(@Param("queryString") String queryString,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);
}
