package com.neu.webserver.service.auth;

import com.neu.webserver.entity.user.Role;
import com.neu.webserver.entity.user.User;
import com.neu.webserver.exception.auth.AlreadyExistsException;
import com.neu.webserver.exception.auth.MissingRegisterInformationException;
import com.neu.webserver.protocol.auth.request.AuthRequest;
import com.neu.webserver.protocol.auth.request.RegisterRequest;
import com.neu.webserver.protocol.auth.response.AuthResponse;
import com.neu.webserver.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse signUp(RegisterRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isPresent()) {
            throw new AlreadyExistsException("The email has been registered");
        }

        User newUser;
        try {
            newUser = User
                    .builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
        } catch (NullPointerException | IllegalArgumentException | AuthenticationException e) {
            throw new MissingRegisterInformationException("Missing information to register an account");
        }

        userRepository.save(newUser);

        String token = jwtService.signToken(newUser);
        return new AuthResponse(token);
    }

    public AuthResponse signIn(AuthRequest request) {
        // email and password are correct
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.signToken(user);
        return new AuthResponse(token);
    }
}
