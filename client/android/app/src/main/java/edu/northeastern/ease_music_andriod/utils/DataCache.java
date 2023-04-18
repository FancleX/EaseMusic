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
        private String token;
        private String username;
        private List<MusicItem> favorites;

        public UserCache() {
            this.favorites = new ArrayList<>();
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

        public boolean isUserLogin() {
            return token != null;
        }

        public List<MusicItem> getFavorites() {
            return favorites;
        }

        public void setFavorites(List<MusicItem> favorites) {
            this.favorites = favorites;
        }

        public boolean checkFavoriteById(String uuid) {
            List<MusicItem> favorites = getFavorites();

            return favorites.stream().anyMatch(musicItem -> musicItem.getUuid().equals(uuid));
        }
    }
}
