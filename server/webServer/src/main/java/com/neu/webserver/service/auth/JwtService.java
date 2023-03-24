package com.neu.webserver.service.auth;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    /**
     * Get username from jwt token.
     *
     * @param token token
     * @return email
     */
    String extractUsername(String token);

    /**
     * Extract a claim from token.
     *
     * @param token          token
     * @param claimsResolver claims resolver function
     * @param <T>            the property type
     * @return the property in the claim
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Generate a token for the given user.
     *
     * @param claims      claims to be put in the token
     * @param userDetails user details
     * @return a signed jwt token
     */
    String signToken(Map<String, Object> claims, UserDetails userDetails);

    /**
     * Generate a token for the given user.
     *
     * @param userDetails user details
     * @return a signed jwt token
     */
    String signToken(UserDetails userDetails);

    /**
     * Verify a token based on provided user details.
     *
     * @param token       jwt token
     * @param userDetails the user details
     * @return true if the token is valid, otherwise false
     */
    boolean verifyToken(String token, UserDetails userDetails);
}
