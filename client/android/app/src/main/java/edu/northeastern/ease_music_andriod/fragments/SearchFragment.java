package edu.northeastern.ease_music_andriod.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.activities.DashBoardActivity;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItemAdapter;
import edu.northeastern.ease_music_andriod.utils.APIRequestGenerator;
import edu.northeastern.ease_music_andriod.utils.DataCache;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;
import edu.northeastern.ease_music_andriod.utils.RequestAPIs;

public class SearchFragment extends Fragment implements APIRequestGenerator.RequestCallback, MusicPlayer.OnUpdateCallback {

    // ================ fields ================
    private final APIRequestGenerator requestGenerator = APIRequestGenerator.getInstance();
    private final MusicPlayer musicPlayer = MusicPlayer.getInstance();
    private static final String TAG = "Search Fragment";
    private final DataCache dataCache = DataCache.getInstance();
    private final AtomicBoolean onLoadingMoreData = new AtomicBoolean(false);
    private final AtomicBoolean onSearchProgress = new AtomicBoolean(false);

    // ================ views ================
    private SearchView searchBar;
    private RecyclerView musicRecycler;
    private MusicItemAdapter musicItemAdapter;
    private final DashBoardActivity dashBoardActivity;

    public SearchFragment(DashBoardActivity dashBoardActivity) {
        this.dashBoardActivity = dashBoardActivity;
    }

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

        musicPlayer.attachOnUpdateCallback(this);

        return root;
    }

    @Override
    public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
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
    }

    @Override
    public void onError(String errorMessage, RequestAPIs.APILabel label) {
        onLoadingMoreData.set(false);
        onSearchProgress.set(false);

        dashBoardActivity.runOnUiThread(() -> Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show());
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

    public void requestAudioResource(MusicItem musicItem, int position) {
        requireActivity().runOnUiThread(this::renderSearchAndMiniPlayerFragments);
//        requestGenerator.accessResource(musicItem.getUuid(), SearchFragment.this);
        musicPlayer.requestMusic(musicItem, position);
    }

    private void renderSearchAndMiniPlayerFragments() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottom_navigation);
        MenuItem item = navigationView.getMenu().findItem(R.id.music);
        item.setChecked(true);

        fragmentTransaction.replace(R.id.frame_layout, new MusicFragment());
        fragmentTransaction.replace(R.id.top_view_panel, new MiniPlayerFragment());

        fragmentTransaction.commit();
    }

    @Override
    public void onNext(String nextMusicId, int nextPosition) {
        if (nextMusicId == null)
            return;

        RecyclerView.LayoutManager layoutManager = musicRecycler.getLayoutManager();
        if (layoutManager != null) {
            View lastView = layoutManager.getChildAt(nextPosition - 1);
            assert lastView != null;
            RecyclerView.ViewHolder lastViewHolder = musicRecycler.getChildViewHolder(lastView);
            TextView lastMusicTitle = lastViewHolder.itemView.findViewById(R.id.music_title);
            lastMusicTitle.setTextColor(Color.parseColor("#595d63"));

            View nextView = layoutManager.getChildAt(nextPosition);
            assert nextView != null;
            RecyclerView.ViewHolder nextViewHolder = musicRecycler.getChildViewHolder(nextView);
            TextView nextMusicTitle = nextViewHolder.itemView.findViewById(R.id.music_title);
            nextMusicTitle.setTextColor(Color.parseColor("#39C5BB"));
        }
    }

    @Override
    public void onLast(String lastMusicId, int lastPosition) {
        if (lastMusicId == null)
            return;

        RecyclerView.LayoutManager layoutManager = musicRecycler.getLayoutManager();
        if (layoutManager != null) {
            View lastView = layoutManager.getChildAt(lastPosition + 1);
            assert lastView != null;
            RecyclerView.ViewHolder lastViewHolder = musicRecycler.getChildViewHolder(lastView);
            TextView lastMusicTitle = lastViewHolder.itemView.findViewById(R.id.music_title);
            lastMusicTitle.setTextColor(Color.parseColor("#595d63"));

            View nextView = layoutManager.getChildAt(lastPosition);
            assert nextView != null;
            RecyclerView.ViewHolder nextViewHolder = musicRecycler.getChildViewHolder(nextView);
            TextView nextMusicTitle = nextViewHolder.itemView.findViewById(R.id.music_title);
            nextMusicTitle.setTextColor(Color.parseColor("#39C5BB"));
        }
    }

}