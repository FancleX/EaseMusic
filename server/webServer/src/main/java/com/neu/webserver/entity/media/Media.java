package com.neu.webserver.entity.media;

import com.neu.webserver.protocol.media.MediaPreview;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String uuid;
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    private String thumbnail;
    private String author;
    private String audioPath;
    private String hashCode;
    @ElementCollection(targetClass = String.class)
    private Set<String> relatedTopics;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Media media = (Media) o;
        return Objects.equals(uuid, media.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    public <T> T extractFromMedia(Function<Media, T> extractor) {
        return extractor.apply(this);
    }

    public MediaPreview mediaPreviewExtractor() {
        return MediaPreview
                .builder()
                .uuid(uuid)
                .title(title)
                .author(author)
                .thumbnail(thumbnail)
                .description(description)
                .build();
    }
}
