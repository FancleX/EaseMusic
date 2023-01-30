package com.neu.webserver.service.user;

import com.neu.webserver.entity.media.MediaShort;
import com.neu.webserver.entity.user.User;
import com.neu.webserver.exception.user.IncorrectPasswordException;
import com.neu.webserver.protocol.user.request.FavoriteUpdateRequest;
import com.neu.webserver.protocol.user.request.PasswordRequest;
import com.neu.webserver.protocol.user.request.UsernameRequest;
import com.neu.webserver.protocol.user.response.FavoriteUpdateResponse;
import com.neu.webserver.protocol.user.response.PasswordResponse;
import com.neu.webserver.protocol.user.response.UsernameResponse;
import com.neu.webserver.repository.media.MediaShortRepository;
import com.neu.webserver.repository.user.UserRepository;
import com.neu.webserver.service.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MediaShortRepository mediaShortRepository;

    @Override
    public UsernameResponse updateUsername(UserDetails userDetails, UsernameRequest request) {
        return null;
    }

    @Override
    @Transactional
    public PasswordResponse updatePassword(UserDetails userDetails, PasswordRequest request) {
        System.out.println(userDetails.getPassword());
        if (!passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
            throw new IncorrectPasswordException("Incorrect old password");
        }
        String password = request.getNewPassword();
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.updatePassword(userDetails.getUsername(), encodedPassword);

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow();

        String token = jwtService.signToken(user);
        return new PasswordResponse(token);
    }

    @Override
    public FavoriteUpdateResponse getFavorites(UserDetails userDetails, int currentIndex, int limit) {
        List<String> favorites = userRepository
                .getOrderedFavorites(
                        userDetails.getUsername(),
                        limit,
                        currentIndex * limit
                );

        return FavoriteUpdateResponse
                .builder()
                .favorites(favorites)
                .currentIndex(currentIndex)
                .limit(limit)
                .build();
    }

    @Override
    public FavoriteUpdateResponse addFavorite(UserDetails userDetails, FavoriteUpdateRequest request) {
        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow();

        MediaShort media = MediaShort
                .builder()
                .user(user)
                .uuid(request.getUuid())
                .addedDate(new Date())
                .build();

        try {
            mediaShortRepository.save(media);
        } catch (Exception ignore) {
            // ignore duplicate add exception
        }

        List<String> favorites = userRepository
                .getOrderedFavorites(
                        user.getEmail(),
                        request.getLimit(),
                        request.getCurrentIndex() * request.getLimit()
                );

        return FavoriteUpdateResponse
                .builder()
                .favorites(favorites)
                .currentIndex(request.getCurrentIndex())
                .limit(request.getLimit())
                .build();
    }

    @Override
    public FavoriteUpdateResponse removeFavorite(UserDetails userDetails, FavoriteUpdateRequest request) {
        return null;
    }
}
