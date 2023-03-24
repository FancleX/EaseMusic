package com.neu.webserver.repository.user;

import com.neu.webserver.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE _user SET password = :password WHERE email = :email", nativeQuery = true)
    void updatePassword(@Param("email") String email, @Param("password") String password);

    @Modifying
    @Query(value = "UPDATE _user SET username = :username WHERE email = :email", nativeQuery = true)
    void updateUsername(@Param("email") String email, @Param("username") String username);

    @Query(value = """
                         SELECT user_favorites.uuid, m.title, m.author, m.thumbnail, m.description
                         FROM user_favorites
                         JOIN _user u ON u.id = user_favorites.user_id
                         JOIN media m ON user_favorites.uuid = m.uuid
                         WHERE u.email = :email
                         ORDER BY user_favorites.added_date ASC
                         LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Map<String, ?>> getOrderedFavorites(@Param("email") String email,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);
}
