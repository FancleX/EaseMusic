package com.neu.webserver.controller.auth;

import com.neu.webserver.protocol.auth.request.AuthRequest;
import com.neu.webserver.protocol.auth.request.RegisterRequest;
import com.neu.webserver.protocol.auth.response.AuthResponse;
import com.neu.webserver.service.auth.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponse> signUp(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }
}
