package com.neu.webserver.entity.media;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "media", indexes = @Index(columnList = "uuid", unique = true))
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    private String thumbnail;
    private String author;
    private String audioPath;
    private String hashCode;
    private long size;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
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
