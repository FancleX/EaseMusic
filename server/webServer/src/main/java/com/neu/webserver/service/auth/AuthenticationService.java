package com.neu.webserver.service.auth;

import com.neu.webserver.protocol.auth.request.AuthRequest;
import com.neu.webserver.protocol.auth.request.RegisterRequest;
import com.neu.webserver.protocol.auth.response.AuthResponse;

public interface AuthenticationService {

    /**
     * User sign up.
     *
     * @param request request include sign up information
     * @return response with authenticate message
     */
    AuthResponse signUp(RegisterRequest request);

    /**
     * User sign in.
     *
     * @param request request include sign in information
     * @return response with authenticate message
     */
    AuthResponse signIn(AuthRequest request);
}
