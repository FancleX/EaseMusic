package edu.northeastern.ease_music_andriod.utils;

public interface RequestAPIs {

    // ============= Auth =============
    void signIn(String email, String password, APIRequestGenerator.RequestCallback callback);

    void signUp(String email, String username, String password, APIRequestGenerator.RequestCallback callback);

    // ============= User =============
    void updateUsername(String token, String username, APIRequestGenerator.RequestCallback callback);

    void updatePassword(String token, String newPassword, String oldPassword, APIRequestGenerator.RequestCallback callback);

    void getFavorites(String token, int index, int limit, APIRequestGenerator.RequestCallback callback);

    void addFavorites(String token, String uuid, int currentIndex, int limit, APIRequestGenerator.RequestCallback callback);

    void removeFavorites(String token, String uuid, int currentIndex, int limit, APIRequestGenerator.RequestCallback callback);

    // ============= Search =============
    void searchContent(String queryString, int pageIndex, APIRequestGenerator.RequestCallback callback);

    void accessResource(String uuid, APIRequestGenerator.RequestCallback callback);

    enum APILabel {
        SIGNIN,
        SIGNUP,
        UPDATE_USERNAME,
        UPDATE_PASSWORD,
        GET_FAVORITES,
        ADD_FAVORITES,
        REMOVE_FAVORITES,
        SEARCH_CONTENT,
        ACCESS_RESOURCE
    }
}
