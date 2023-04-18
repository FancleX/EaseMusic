package edu.northeastern.ease_music_andriod.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIRequestGenerator implements RequestAPIs {

    private static volatile APIRequestGenerator instance;
    private static final String[] URLS = new String[] {
            "http://100.25.191.181:9000/api/v1/auth/signIn",
            "http://100.25.191.181:9000/api/v1/auth/signUp",
            "http://100.25.191.181:9000/api/v1/user/update/username",
            "http://100.25.191.181:9000/api/v1/user/update/password",
            "http://100.25.191.181:9000/api/v1/user/get/favorites?index=%d&limit=%d",
            "http://100.25.191.181:9000/api/v1/user/add/favorites",
            "http://100.25.191.181:9000/api/v1/user/delete/favorites",
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

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException ignored) {}

        RequestBody requestBody = RequestBody.create(jsonObject.toString(),MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());

                callback.onError(e.getMessage(), APILabel.SIGNIN);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(responseString);
                            String token = jsonObject.getString("token");

                            Log.i(TAG, "Token: " + token);
                            callback.onSuccess(jsonObject, APILabel.SIGNIN);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onError(jsonObject.getString("message"), APILabel.SIGNIN);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.SIGNIN);
            }
        });

    }

    @Override
    public void signUp(String email, String username, String password, RequestCallback callback) {
        String url = URLS[1], method = METHODS[1];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException ignored) {}

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), mediaType);
        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());
                callback.onError(e.getMessage(), APILabel.SIGNUP);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onSuccess(jsonObject, APILabel.SIGNUP);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onError(jsonObject.getString("message"), APILabel.SIGNUP);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        callback.onError(e.getMessage(), APILabel.SIGNUP);
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.SIGNUP);
            }
        });
    }

    @Override
    public void updateUsername(String token, String username, RequestCallback callback) {
        String url = URLS[2], method = METHODS[2];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException ignored) {}

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());
                callback.onError(e.getMessage(), APILabel.UPDATE_USERNAME);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(responseString);
                            Log.i(TAG, jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.UPDATE_USERNAME);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onError(jsonObject.getString("message"), APILabel.UPDATE_USERNAME);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.UPDATE_USERNAME);
            }
        });
    }

    @Override
    public void updatePassword(String token, String newPassword, String oldPassword, RequestCallback callback) {
        String url = URLS[3], method = METHODS[2];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("new_password", newPassword);
            jsonObject.put("old_password", oldPassword);
        } catch (JSONException ignored) {}

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .header("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());
                callback.onError(e.getMessage(), APILabel.UPDATE_PASSWORD);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("message", "Password updated successfully");

                            Log.i(TAG, jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.UPDATE_PASSWORD);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onError(jsonObject.getString("message"), APILabel.UPDATE_PASSWORD);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.UPDATE_PASSWORD);
            }
        });

    }

    @Override
    public void getFavorites(String token, int index, int limit, RequestCallback callback) {
        String url = String.format(Locale.US, URLS[4], index, limit), method = METHODS[0];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method(method, null)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());

                callback.onError(e.getMessage(), APILabel.GET_FAVORITES);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        if (responseString.isEmpty()) {
                            callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.GET_FAVORITES);
                            return;
                        }

                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(responseString);

                            Log.i(TAG, jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.GET_FAVORITES);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onError(jsonObject.getString("message"), APILabel.GET_FAVORITES);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.GET_FAVORITES);
            }
        });
    }

    @Override
    public void addFavorites(String token, String uuid, int currentIndex, int limit, RequestCallback callback) {
        String url = URLS[5], method = METHODS[2];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", uuid);
            jsonObject.put("currentIndex", String.valueOf(currentIndex));
            jsonObject.put("limit", String.valueOf(limit));
        } catch (JSONException ignored) {}

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());

                callback.onError(e.getMessage(), APILabel.ADD_FAVORITES);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        if (responseString.isEmpty()) {
                            callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.ADD_FAVORITES);
                            return;
                        }

                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(responseString);

                            Log.i(TAG, jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.ADD_FAVORITES);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onError(jsonObject.getString("message"), APILabel.ADD_FAVORITES);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.ADD_FAVORITES);
            }
        });
    }

    @Override
    public void removeFavorites(String token, String uuid, int currentIndex, int limit, RequestCallback callback) {
        String url = URLS[6], method = METHODS[2];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", uuid);
            jsonObject.put("currentIndex", String.valueOf(currentIndex));
            jsonObject.put("limit", String.valueOf(limit));
        } catch (JSONException ignored) {}

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());

                callback.onError(e.getMessage(), APILabel.REMOVE_FAVORITES);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        if (responseString.isEmpty()) {
                            callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.REMOVE_FAVORITES);
                            return;
                        }

                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(responseString);

                            Log.i(TAG, jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.REMOVE_FAVORITES);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
                            callback.onError(jsonObject.getString("message"), APILabel.REMOVE_FAVORITES);
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }

                callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.REMOVE_FAVORITES);
            }
        });
    }

    @Override
    public void searchContent(String queryString, int pageIndex, RequestCallback callback) {
        String query = Pattern.compile("[|&]").matcher(queryString).replaceAll(" ").trim();

        String url = String.format(Locale.US, URLS[7], query, pageIndex), method = METHODS[0];

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
                        String responseString = response.body().string();

                        if (responseString.isEmpty()) {
                            callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.ADD_FAVORITES);
                            return;
                        }

                        if (response.code() == 200) {
                            JSONArray jsonArray = new JSONArray(responseString);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("data", jsonArray);

                            Log.i(TAG, jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.SEARCH_CONTENT);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            Log.e(TAG, String.format("Code: %d, error: %s", response.code(), responseString));
                            JSONObject jsonObject = new JSONObject(responseString);
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
                .readTimeout(90, TimeUnit.SECONDS)
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
                        String responseString = response.body().string();

                        if (responseString.isEmpty()) {
                            callback.onError(EMPTY_RESPONSE_BODY_ERROR, APILabel.ADD_FAVORITES);
                            return;
                        }

                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(responseString);

                            Log.i(TAG, jsonObject.toString());
                            callback.onSuccess(jsonObject, APILabel.ACCESS_RESOURCE);
                        } else if (response.code() >= 400 && response.code() < 500) {
                            JSONObject jsonObject = new JSONObject(responseString);
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
