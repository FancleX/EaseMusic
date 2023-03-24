package com.neu.webserver.controller.user;

import com.neu.webserver.protocol.user.request.FavoriteUpdateRequest;
import com.neu.webserver.protocol.user.request.PasswordRequest;
import com.neu.webserver.protocol.user.request.UsernameRequest;
import com.neu.webserver.protocol.user.response.FavoriteUpdateResponse;
import com.neu.webserver.protocol.user.response.PasswordResponse;
import com.neu.webserver.protocol.user.response.UsernameResponse;
import com.neu.webserver.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // change username
    @PutMapping("/update/username")
    public ResponseEntity<UsernameResponse> updateUsername(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UsernameRequest request
    ) {
        return ResponseEntity.ok(userService.updateUsername(userDetails, request));
    }

    // change password
    @PutMapping("/update/password")
    public ResponseEntity<PasswordResponse> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordRequest request
    ) {
        return ResponseEntity.ok(userService.updatePassword(userDetails, request));
    }

    // get all favorites

    @GetMapping("/get/favorites")
    public ResponseEntity<FavoriteUpdateResponse> getFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("index") int currentIndex,
            @RequestParam("limit") int limit
    ) {
        return ResponseEntity.ok(userService.getFavorites(userDetails, currentIndex, limit));
    }

    // add to favorite list
    @PutMapping("/add/favorites")
    public ResponseEntity<FavoriteUpdateResponse> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FavoriteUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.addFavorite(userDetails, request));
    }

    // remove from favorite list
    @PutMapping("/delete/favorites")
    public ResponseEntity<FavoriteUpdateResponse> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FavoriteUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.removeFavorite(userDetails, request));
    }

}
