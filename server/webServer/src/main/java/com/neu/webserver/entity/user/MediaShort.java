package com.neu.webserver.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"uuid", "user_id"}))
public class MediaShort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uuid")
    private String uuid;

    // use for sorting
    private Date addedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaShort that)) return false;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
