package com.neu.webserver.config.auth;

import com.neu.webserver.entity.user.User;
import com.neu.webserver.repository.user.UserRepository;
import com.neu.webserver.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityConfig securityConfig;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, SecurityConfig securityConfig) {
        this.jwtService = jwtService;
        this.securityConfig = securityConfig;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // get auth from header
        final String header = request.getHeader("Authorization");

        // reject null token request
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get user email from jwt token
        final String token = header.substring(7).trim();
        final String userEmail = jwtService.extractUsername(token);
        // if the user is not authenticated yet but exists in database
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails = securityConfig.userDetailsService().loadUserByUsername(userEmail);
            // if token is valid, update security context
            if (jwtService.verifyToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
