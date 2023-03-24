package com.neu.webserver.repository.media;

import com.neu.webserver.entity.user.MediaShort;
import com.neu.webserver.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaShortRepository extends JpaRepository<MediaShort, Integer> {
    Optional<MediaShort> findByUserAndUuid(User user, String uuid);
}
