package edu.northeastern.ease_music_andriod.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItemAdapter;
import edu.northeastern.ease_music_andriod.utils.APIRequestGenerator;
import edu.northeastern.ease_music_andriod.utils.DataCache;
import edu.northeastern.ease_music_andriod.utils.RequestAPIs;

public class SearchFragment extends Fragment implements APIRequestGenerator.RequestCallback {

    // ================ fields ================
    private final APIRequestGenerator requestGenerator = APIRequestGenerator.getInstance();
    private static final String TAG = "Search Fragment";
    private final DataCache dataCache = DataCache.getInstance();
    private final AtomicBoolean onLoadingMoreData = new AtomicBoolean(false);
    private final AtomicBoolean onSearchProgress = new AtomicBoolean(false);

    // ================ views ================
    private SearchView searchBar;
    private RecyclerView musicRecycler;
    private MusicItemAdapter musicItemAdapter;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        // initiate search bar
        searchBar = root.findViewById(R.id.search_bar);
        initiateSearchBar();

        // initiate recycler view
        musicRecycler = root.findViewById(R.id.music_recycler);
        musicRecycler.setHasFixedSize(true);
        musicItemAdapter = new MusicItemAdapter(SearchFragment.this);
        musicRecycler.setAdapter(musicItemAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false);
        musicRecycler.setLayoutManager(layoutManager);
        musicRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!onLoadingMoreData.get()
                        && !onSearchProgress.get()
                        && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    // reached bottom, load more data
                    if (!dataCache.getSearchCache().hasNoMoreData())
                        loadMoreSearchResult(dataCache.getSearchCache().getPageIndex() + 1);
                }
            }
        });

        if (dataCache.getSearchCache().hasCachedData())
            musicItemAdapter.updateData();

        return root;
    }

    @Override
    public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
        switch (label) {
            case SEARCH_CONTENT:
                try {
                    JSONArray data = response.getJSONArray("data");
                    final ArrayList<MusicItem> musicItems = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);

                        MusicItem item = new MusicItem(
                                object.getString("uuid"),
                                object.getString("title"),
                                object.getString("author"),
                                object.getString("description"),
                                object.getString("thumbnail")
                        );

                        musicItems.add(item);
                    }

                    dataCache.getSearchCache().cacheResultList(musicItems);

                    requireActivity().runOnUiThread(() -> musicItemAdapter.updateData());
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    onLoadingMoreData.set(false);
                    onSearchProgress.set(false);
                }
                break;
            case ACCESS_RESOURCE:
                break;
        }
    }

    @Override
    public void onError(String errorMessage, RequestAPIs.APILabel label) {
        switch (label) {
            case SEARCH_CONTENT:
                onLoadingMoreData.set(false);
                onSearchProgress.set(false);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                break;
            case ACCESS_RESOURCE:
                break;
        }
    }

    private void initiateSearchBar() {
        searchBar.clearFocus();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!onSearchProgress.get()) {
                    onSearchProgress.set(true);
                    dataCache.getSearchCache().clearCacheBuffer();
                    dataCache.getSearchCache().setQueryString(query);
                    dataCache.getSearchCache().setPageIndex(0);
                    requestGenerator.searchContent(query, 0, SearchFragment.this);
                }

                hideKeyboard(musicRecycler);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void loadMoreSearchResult(int pageIndex) {
        onLoadingMoreData.set(true);
        requestGenerator.searchContent(dataCache.getSearchCache().getQueryString(), pageIndex, SearchFragment.this);
        dataCache.getSearchCache().setPageIndex(pageIndex);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}