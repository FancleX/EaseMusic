package edu.northeastern.ease_music_andriod.utils;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;

public class DataCache {

    private static volatile DataCache instance;

    private final SearchCache<MusicItem> searchCache;

    private DataCache() {
        this.searchCache = new SearchCache<>();
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

}
