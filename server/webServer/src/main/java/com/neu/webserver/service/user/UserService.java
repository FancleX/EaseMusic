package com.neu.webserver.service.user;

import com.neu.webserver.protocol.user.request.FavoriteUpdateRequest;
import com.neu.webserver.protocol.user.request.PasswordRequest;
import com.neu.webserver.protocol.user.request.UsernameRequest;
import com.neu.webserver.protocol.user.response.FavoriteUpdateResponse;
import com.neu.webserver.protocol.user.response.PasswordResponse;
import com.neu.webserver.protocol.user.response.UsernameResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    /**
     * Update username.
     *
     * @param userDetails authenticated user
     * @param request request to update username
     * @return response with the username
     */
    UsernameResponse updateUsername(UserDetails userDetails, UsernameRequest request);

    /**
     * Update password.
     *
     * @param userDetails authenticated user
     * @param request request to update password
     * @return response with a new signed token
     */
    PasswordResponse updatePassword(UserDetails userDetails, PasswordRequest request);

    /**
     * Get all favorites of the user.
     *
     * @param userDetails authenticated user
     * @param currentIndex current page index
     * @param limit limit per page
     * @return response with a sorted favorites list in time according order by page
     */
    FavoriteUpdateResponse getFavorites(UserDetails userDetails, int currentIndex, int limit);

    /**
     * Add a media to favorites.
     *
     * @param userDetails authenticated user
     * @param request request to add a media to favorites
     * @return response with a sorted favorites list in time according order
     */
    FavoriteUpdateResponse addFavorite(UserDetails userDetails, FavoriteUpdateRequest request);

    /**
     * Remove a media from favorites.
     *
     * @param userDetails authenticated user
     * @param request request to remove a media to favorites
     * @return response with a sorted favorites list in time according order
     */
    FavoriteUpdateResponse removeFavorite(UserDetails userDetails, FavoriteUpdateRequest request);

}
