package edu.northeastern.ease_music_andriod.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.musicItem.MusicItem;

public class UserService {

    private static final String TAG = "User Service";
    private static volatile UserService instance;
    private final DataCache dataCache = DataCache.getInstance();
    private final APIRequestGenerator requestGenerator = APIRequestGenerator.getInstance();
    private OnRequestResultListener onRequestResultListener;

    private UserService() {
    }

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

    public void signIn(String email, String password) {
        requestGenerator.signIn(email, password, new APIRequestGenerator.RequestCallback() {
            @Override
            public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                try {
                    String token = response.getString("token");

                    dataCache.getUserCache().setToken(token);
                    dataCache.getUserCache().setUsername(email);

                    // synchronize the request
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    getFavorites(token, countDownLatch);
                    countDownLatch.await();

                    if (onRequestResultListener != null)
                        onRequestResultListener.onSuccess("Successfully sign in", RequestAPIs.APILabel.SIGNIN);
                } catch (JSONException | InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage, RequestAPIs.APILabel label) {
                Log.e(TAG, label.toString() + ": " + errorMessage);
                if (onRequestResultListener != null)
                    onRequestResultListener.onError("Unable to sign in, email or password incorrect", RequestAPIs.APILabel.SIGNIN);
            }
        });
    }

    public void signUp(String username, String email, String password) {
        requestGenerator.signUp(email, username, password, new APIRequestGenerator.RequestCallback() {
            @Override
            public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                try {
                    String token = response.getString("token");

                    dataCache.getUserCache().setToken(token);
                    dataCache.getUserCache().setUsername(email);

                    if (onRequestResultListener != null)
                        onRequestResultListener.onSuccess("Successfully sign up", RequestAPIs.APILabel.SIGNUP);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage, RequestAPIs.APILabel label) {
                Log.e(TAG, label.toString() + ": " + errorMessage);
                if (onRequestResultListener != null)
                    onRequestResultListener.onError("Unable to sign up: " + errorMessage, RequestAPIs.APILabel.SIGNUP);
            }
        });
    }

    private void getFavorites(String token, CountDownLatch countDownLatch) {
        DataCache.UserCache userCache = dataCache.getUserCache();

        requestGenerator.getFavorites(
                token,
                0,
                Integer.MAX_VALUE,
                new APIRequestGenerator.RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                        try {
                            JSONArray favoriteList = response.getJSONArray("favorites");

                            ArrayList<MusicItem> favorites = new ArrayList<>();
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

                            userCache.setFavorites(favorites);
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        } finally {
                            countDownLatch.countDown();
                        }
                    }

                    @Override
                    public void onError(String errorMessage, RequestAPIs.APILabel label) {
                        Log.e(TAG, label.toString() + ": " + errorMessage);
                        countDownLatch.countDown();
                        if (onRequestResultListener != null)
                            onRequestResultListener.onError("Unable to fetch favorites: " + errorMessage, RequestAPIs.APILabel.GET_FAVORITES);
                    }
                });

    }


    public void addToFavorite(String uuid) {
        DataCache.UserCache userCache = dataCache.getUserCache();

        requestGenerator.addFavorites(
                dataCache.getUserCache().getToken(),
                uuid,
                0,
                Integer.MAX_VALUE,
                new APIRequestGenerator.RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                        try {
                            JSONArray favoriteList = response.getJSONArray("favorites");

                            ArrayList<MusicItem> favorites = new ArrayList<>();
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

                            userCache.setFavorites(favorites);
                            if (onRequestResultListener != null)
                                onRequestResultListener.onSuccess("Successfully added", RequestAPIs.APILabel.ADD_FAVORITES);

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String errorMessage, RequestAPIs.APILabel label) {
                        Log.e(TAG, label.toString() + ": " + errorMessage);
                        if (onRequestResultListener != null)
                            onRequestResultListener.onError("Unable to add: " + errorMessage, RequestAPIs.APILabel.ADD_FAVORITES);
                    }
                });
    }

    public void removeFavorites(String uuid) {
        DataCache.UserCache userCache = dataCache.getUserCache();

        requestGenerator.removeFavorites(
                dataCache.getUserCache().getToken(),
                uuid,
                0,
                Integer.MAX_VALUE,
                new APIRequestGenerator.RequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                        try {
                            JSONArray favoriteList = response.getJSONArray("favorites");

                            ArrayList<MusicItem> favorites = new ArrayList<>();
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

                            userCache.setFavorites(favorites);
                            if (onRequestResultListener != null)
                                onRequestResultListener.onSuccess("Successfully removed", RequestAPIs.APILabel.REMOVE_FAVORITES);

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String errorMessage, RequestAPIs.APILabel label) {
                        Log.e(TAG, label.toString() + ": " + errorMessage);
                        if (onRequestResultListener != null)
                            onRequestResultListener.onError("Unable to remove: " + errorMessage, RequestAPIs.APILabel.REMOVE_FAVORITES);
                    }
                });
    }

    public interface OnRequestResultListener {

        void onSuccess(String message, RequestAPIs.APILabel label);

        void onError(String error, RequestAPIs.APILabel label);
    }

}
