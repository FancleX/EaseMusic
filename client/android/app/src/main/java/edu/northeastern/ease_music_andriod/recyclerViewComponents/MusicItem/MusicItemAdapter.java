package edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ease_music_andriod.R;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemViewHolder> implements MusicItemViewHolder.OnMusicItemClickListener {

    private final ArrayList<MusicItem> musicList;

    public MusicItemAdapter() {
        this.musicList = new ArrayList<>();
    }


    @NonNull
    @Override
    public MusicItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item_layout, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItemViewHolder holder, int position) {
        holder.bindData(musicList.get(position));
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    @Override
    public void onTextClick(View view, int position) {

    }

    @Override
    public void onBrowserIconClick(View view, int position) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(String.format("https://www.youtube.com/watch?v=%s", musicList.get(position))));

            try {
                view.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(view, "Unable to access the video", Snackbar.LENGTH_SHORT).show();
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAllItems(ArrayList<MusicItem> list) {
        musicList.clear();
        musicList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<MusicItem> getMusicList() {
        return musicList;
    }
}
