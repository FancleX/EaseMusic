package com.neu.webserver.repository.user;

import com.neu.webserver.entity.media.MediaShort;
import com.neu.webserver.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE _user SET password = :password WHERE email = :email", nativeQuery = true)
    void updatePassword(@Param("email") String email, @Param("password") String password);

    @Query(value = "SELECT user_favorites FROM _user WHERE email = :email ORDER BY addedDate ASC OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<MediaShort> getOrderedFavorites(@Param("email") String email, @Param("offset") int offset, @Param("limit") int limit);


}
