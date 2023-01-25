package com.neu.webserver.service.security;

import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface JwtService {

    /**
     * Get user email from jwt token.
     *
     * @param token token
     * @return email
     */
    String extractUserEmail(String token);

    /**
     * Extract a claim from token.
     *
     * @param token token
     * @param claimsResolver function from Claims class
     * @return the property in the claim
     * @param <T> the property type
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
