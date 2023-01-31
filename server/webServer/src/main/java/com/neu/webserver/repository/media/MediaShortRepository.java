package com.neu.webserver.repository.media;

import com.neu.webserver.entity.user.MediaShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaShortRepository extends JpaRepository<MediaShort, Integer> {
}
