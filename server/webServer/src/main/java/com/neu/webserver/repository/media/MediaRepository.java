package com.neu.webserver.repository.media;

import com.neu.webserver.entity.media.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    Optional<Media> findByUuid(String uuid);

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
                SELECT uuid, title, author, description, thumbnail
                FROM media
                INNER JOIN media_related_topics
                    ON media.id = media_related_topics.media_id
                WHERE related_topics LIKE CONCAT('%', :queryString, '%')
                LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Object> getMediaPreviewByPage(@Param("queryString") String queryString,
                                       @Param("limit") int limit,
                                       @Param("offset") int offset);
}
