package com.neu.webserver.service.user;

import com.neu.webserver.entity.media.MediaShort;
import com.neu.webserver.entity.user.User;
import com.neu.webserver.exception.user.IncorrectPasswordException;
import com.neu.webserver.protocol.auth.response.AuthResponse;
import com.neu.webserver.protocol.user.request.FavoriteGetRequest;
import com.neu.webserver.protocol.user.request.FavoriteUpdateRequest;
import com.neu.webserver.protocol.user.request.PasswordRequest;
import com.neu.webserver.protocol.user.request.UsernameRequest;
import com.neu.webserver.protocol.user.response.FavoriteUpdateResponse;
import com.neu.webserver.protocol.user.response.PasswordResponse;
import com.neu.webserver.protocol.user.response.UsernameResponse;
import com.neu.webserver.repository.user.UserRepository;
import com.neu.webserver.service.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsernameResponse updateUsername(UserDetails userDetails, UsernameRequest request) {
        return null;
    }

    @Override
    public PasswordResponse updatePassword(UserDetails userDetails, PasswordRequest request) {
        if (!userDetails.getPassword().equals(passwordEncoder.encode(request.getOldPassword()))) {
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
    public FavoriteUpdateResponse getAllFavorites(UserDetails userDetails, FavoriteGetRequest request) {
        String email = userDetails.getUsername();

        List<MediaShort> favorites = userRepository
                .getOrderedFavorites(
                        email,
                        request.getCurrentIndex() * request.getLimit(),
                        request.getLimit()
                );

        return new FavoriteUpdateResponse(favorites);
    }

    @Override
    public FavoriteUpdateResponse addFavorite(UserDetails userDetails, FavoriteUpdateRequest request) {
        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow();

        MediaShort media = MediaShort
                .builder()
                .uuid(request.getUuid())
                .addedDate(new Date())
                .build();

        user.getFavorites().add(media);

        userRepository.save(user);

        List<MediaShort> favorites = userRepository
                .getOrderedFavorites(
                        user.getEmail(),
                        request.getCurrentIndex() * request.getLimit(),
                        request.getLimit());

        return new FavoriteUpdateResponse(favorites);
    }

    @Override
    public FavoriteUpdateResponse removeFavorite(UserDetails userDetails, FavoriteUpdateRequest request) {
        return null;
    }
}
