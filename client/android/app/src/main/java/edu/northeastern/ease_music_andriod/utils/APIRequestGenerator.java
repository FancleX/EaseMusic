package edu.northeastern.ease_music_andriod.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIRequestGenerator implements RequestAPIs {

    private static volatile APIRequestGenerator instance;
    private static final String[] URLS = new String[] {
            "http://100.25.191.181:9000/api/v1/auth/signIn",
            "http://100.25.191.181:9000/api/v1/auth/signUp",
            "http://100.25.191.181:9000/api/v1/user/update/username",
            "http://100.25.191.181:9000/api/v1/user/update/password",
            "http://100.25.191.181:9000/api/v1/user/get/favorites?index=%d&limit=%d",
            "http://100.25.191.181:9000/api/v1/user/user/add/favorites",
            "http://100.25.191.181:9000/api/v1/user/user/delete/favorites",
            "http://100.25.191.181:9000/api/v1/search/results?search_query=%s&page_index=%d",
            "http://100.25.191.181:9000/api/v1/search/detail?uuid=%s"
    };
    private static final String[] METHODS = new String[] {
            "GET",
            "POST",
            "PUT"
    };
    private static final String EMPTY_RESPONSE_BODY_ERROR = "Request failed";
    private static final String TAG = "API Request Generator";

    private APIRequestGenerator() {}

    public static APIRequestGenerator getInstance() {
        if (instance == null) {
            synchronized (APIRequestGenerator.class) {
                instance = new APIRequestGenerator();
            }
        }

        return instance;
    }

    @Override
    public void signIn(String email, String password, RequestCallback callback) {
        String url = URLS[0], method = METHODS[1];

    }

    @Override
    public void signUp(String email, String username, String password, RequestCallback callback) {
        String url = URLS[1], method = METHODS[1];

    }

    @Override
    public void updateUsername(String token, String username, RequestCallback callback) {
        String url = URLS[2], method = METHODS[2];
    }

    @Override
    public void updatePassword(String token, String newPassword, String oldPassword, RequestCallback callback) {
        String url = URLS[3], method = METHODS[2];
    }

    @Override
    public void getFavorites(String token, int index, int limit, RequestCallback callback) {
        String url = String.format(Locale.US, URLS[4], index, limit), method = METHODS[0];
    }

    @Override
    public void addFavorites(String token, String uuid, int currentIndex, int limit, RequestCallback callback) {
        String url = URLS[5], method = METHODS[2];
    }

    @Override
    public void removeFavorites(String token, String uuid, int currentIndex, int limit, RequestCallback callback) {
        String url = URLS[6], method = METHODS[2];
    }

    @Override
    public void searchContent(String queryString, int pageIndex, RequestCallback callback) {
        String url = String.format(Locale.US, URLS[7], queryString, pageIndex), method = METHODS[0];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method(method, null)
                .header("Connection", "keep-alive")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());

                callback.onError(e.getMessage(), APILabel.SEARCH_CONTENT);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        if (response.code() == 200) {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("data", jsonArray);

                            Log.i("API Request Generator", jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.SEARCH_CONTENT);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            callback.onError(jsonObject.getString("message"), APILabel.SEARCH_CONTENT);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.SEARCH_CONTENT);
            }
        });
    }

    @Override
    public void accessResource(String uuid, RequestCallback callback) {
        String url = String.format(URLS[8], uuid), method = METHODS[0];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept-Ranges", "0-")
                .header("Connection", "keep-alive")
                .method(method, null)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());

                callback.onError(e.getMessage(), APILabel.ACCESS_RESOURCE);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Log.i("API Request Generator", jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.ACCESS_RESOURCE);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            callback.onError(jsonObject.getString("message"), APILabel.ACCESS_RESOURCE);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.ACCESS_RESOURCE);
            }
        });
    }


    public interface RequestCallback {

        void onSuccess(JSONObject response, APILabel label);

        void onError(String errorMessage, APILabel label);
    }

}
