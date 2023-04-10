package edu.northeastern.ease_music_andriod.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;

public class UserService {

    private static volatile UserService instance;
    private final DataCache dataCache = DataCache.getInstance();
    private final APIRequestGenerator requestGenerator = APIRequestGenerator.getInstance();
    private static final String TAG = "User Service";
    private OnRequestResultListener onRequestResultListener;

    private UserService() {}

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                instance = new UserService();
            }
        }

        return instance;
    }

    public void setOnRequestResultListener(OnRequestResultListener requestResultListener) {
        this.onRequestResultListener = requestResultListener;
    }

    public void signIn() {}

    public void signUp() {}

    public void addToFavorite(String uuid) {
        DataCache.UserCache userCache = dataCache.getUserCache();

        requestGenerator.addFavorites(
                dataCache.getUserCache().getToken(),
                uuid,
                userCache.getCurrentFavoritesIndex(),
                5,
                new APIRequestGenerator.RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                        try {
                            JSONArray favoriteList = response.getJSONArray("favorites");

                            List<MusicItem> favorites = new ArrayList<>();
                            for (int i = 0; i < favoriteList.length(); i++) {
                                JSONObject favoriteItem = favoriteList.getJSONObject(i);

                                String uuid = favoriteItem.getString("uuid");
                                String title = favoriteItem.getString("title");
                                String author = favoriteItem.getString("author");
                                String description = favoriteItem.getString("description");
                                String thumbnail = favoriteItem.getString("thumbnail");

                                favorites.add(new MusicItem(
                                        uuid,
                                        title,
                                        author,
                                        description,
                                        thumbnail
                                ));
                            }

                            userCache.setFavorites(favorites, 0);
                            if (onRequestResultListener != null)
                                onRequestResultListener.onSuccess(OnRequestResultListener.EventType.ADD);

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String errorMessage, RequestAPIs.APILabel label) {
                        Log.e(TAG, label.toString() + ": " + errorMessage);
                        if (onRequestResultListener != null)
                            onRequestResultListener.onError(errorMessage, OnRequestResultListener.EventType.ADD);
                    }
                });
    }
    public void removeFavorites(String uuid) {}
    public void loadMoreFavorites() {

    }


    public boolean checkFavoriteById(String uuid) {
        List<MusicItem> favorites = dataCache.getUserCache().getFavorites();

        return favorites.stream().anyMatch(musicItem -> musicItem.getUuid().equals(uuid));
    }

    public interface OnRequestResultListener {
        enum EventType {
            ADD,
            REMOVE,
            GET
        }

        void onSuccess(EventType eventType);
        void onError(String error, EventType eventType);
    }

}
