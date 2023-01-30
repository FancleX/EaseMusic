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

    @Query(value = "SELECT user_favorites.uuid FROM _user " +
            "INNER JOIN user_favorites ON _user.id = user_favorites.user_id WHERE email = :email " +
            "ORDER BY added_date ASC LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<String> getOrderedFavorites(@Param("email") String email, @Param("limit") int limit, @Param("offset") int offset);


}
