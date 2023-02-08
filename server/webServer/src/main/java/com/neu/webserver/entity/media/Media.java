package com.neu.webserver.entity.media;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;
    @Column(unique = true)
    private String uuid;
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    private String thumbnail;
    private String author;
    @Nullable
    private String audioPath;
    @Nullable
    private String hashCode;
    @ElementCollection(targetClass = String.class)
    @Nullable
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
}
