package com.neu.webserver.controller.auth;

import com.neu.webserver.protocol.auth.request.AuthRequest;
import com.neu.webserver.protocol.auth.request.RegisterRequest;
import com.neu.webserver.protocol.auth.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody RegisterRequest request) {
        return null;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest request) {
        return null;
    }
}
