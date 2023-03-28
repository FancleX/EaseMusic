package edu.northeastern.ease_music_andriod.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItemAdapter;
import edu.northeastern.ease_music_andriod.utils.APIRequestGenerator;
import edu.northeastern.ease_music_andriod.utils.RequestAPIs;

public class SearchFragment extends Fragment implements APIRequestGenerator.RequestCallback {

    // ================ fields ================
    private final APIRequestGenerator requestGenerator = APIRequestGenerator.getInstance();
    private static final String TAG = "Search Fragment";
    private static final String LATEST_SEARCH_RESULT = "LATEST_SEARCH_RESULT";

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
        musicItemAdapter = new MusicItemAdapter();
        musicRecycler.setAdapter(musicItemAdapter);
        musicRecycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        if (savedInstanceState != null) {
            ArrayList<MusicItem> savedSearch = savedInstanceState.getParcelableArrayList(LATEST_SEARCH_RESULT);
            musicItemAdapter.addAllItems(savedSearch);
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(LATEST_SEARCH_RESULT, musicItemAdapter.getMusicList());
        super.onSaveInstanceState(outState);
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

                    requireActivity().runOnUiThread(() -> musicItemAdapter.addAllItems(musicItems));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
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
                requestGenerator.searchContent(query, 0, SearchFragment.this);
                hideKeyboard(musicRecycler);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void renderMusicList() {

    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}