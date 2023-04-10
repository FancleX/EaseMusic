package edu.northeastern.ease_music_andriod.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;

public class DataCache {

    private static volatile DataCache instance;

    private final SearchCache<MusicItem> searchCache;
    private final UserCache userCache;

    private DataCache() {
        this.searchCache = new SearchCache<>();
        this.userCache = new UserCache();
    }

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    public SearchCache<MusicItem> getSearchCache() {
        return searchCache;
    }
    public UserCache getUserCache() {
        return userCache;
    }


    public static class SearchCache<T> {
        private int pageIndex;
        private final ArrayList<T> resultList;
        private String queryString;
        private boolean hasNoMoreData;

        public SearchCache() {
            this.resultList = new ArrayList<>();
        }

        public boolean hasCachedData() {
            return !resultList.isEmpty();
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public ArrayList<T> getResultList() {
            return resultList;
        }

        public void cacheResultList(ArrayList<T> resultList) {
            if (resultList.isEmpty()) {
                hasNoMoreData = true;
                return;
            }
            this.resultList.addAll(resultList);
        }

        public void clearCacheBuffer() {
            pageIndex = 0;
            resultList.clear();
            queryString = null;
            hasNoMoreData = false;
        }

        public String getQueryString() {
            return queryString;
        }

        public void setQueryString(String queryString) {
            this.queryString = queryString;
        }

        public boolean hasNoMoreData() {
            return hasNoMoreData;
        }
    }

    public static class UserCache {
        private String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYW5jbGVAZ21haWwuY29tIiwiaWF0IjoxNjgxMDc5NjIzLCJleHAiOjE2ODExNjYwMjN9.jBjupD4MPLsanfycCE3WVfZeUELzc8Vl2ihLJ8puicE";
        private String username;
        private final Map<Integer, List<MusicItem>> favoritesMap;
        private final AtomicBoolean isLogin = new AtomicBoolean(true);
        private int currentFavoritesIndex = 0;
        private boolean hasNoMoreData;

        public UserCache() {
            this.favoritesMap = new TreeMap<>();
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<MusicItem> getFavorites() {
            final List<MusicItem> list = new ArrayList<>();
            favoritesMap.values().forEach(list::addAll);

            return list;
        }

        public void setFavorites(List<MusicItem> favorites, int index) {
            if (favorites.isEmpty()) {
                hasNoMoreData = true;
                return;
            }

            favoritesMap.put(index, favorites);
            Log.i("Data cache", favoritesMap.toString());
        }

        public int getCurrentFavoritesIndex() {
            return currentFavoritesIndex;
        }

        public void setCurrentFavoritesIndex(int currentFavoritesIndex) {
            this.currentFavoritesIndex = currentFavoritesIndex;
        }

        public boolean isUserLogin() {
            return isLogin.get();
        }
    }
}
