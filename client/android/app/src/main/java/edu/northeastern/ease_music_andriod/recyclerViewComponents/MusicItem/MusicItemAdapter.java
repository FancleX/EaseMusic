package edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.utils.DataCache;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemViewHolder> implements MusicItemViewHolder.OnMusicItemClickListener {

    private ArrayList<MusicItem> musicList;
    private final Fragment mainFragment;

    public MusicItemAdapter(Fragment mainFragment) {
        this.mainFragment = mainFragment;
    }


    @NonNull
    @Override
    public MusicItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item_layout, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItemViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.bindData(musicList.get(position));
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return musicList == null ? 0 : musicList.size();
    }


    @Override
    public void onTextClick(View view, int position) {

    }

    @Override
    public void onBrowserIconClick(View view, int position) {
        MusicItem musicItem = musicList.get(position);
        String uuid = musicItem.getUuid();

        Log.i("Music Adapter", uuid);

        AlertDialog.Builder builder = new AlertDialog.Builder(mainFragment.getContext());
        LayoutInflater inflater = LayoutInflater.from(mainFragment.getContext());
        View dialogView = inflater.inflate(R.layout.youtube_player_layout, null);
        builder.setView(dialogView);

        YouTubePlayerView playerView = dialogView.findViewById(R.id.youtube_player_view);
        // Initialize the YouTubePlayer instance

        mainFragment.getLifecycle().addObserver(playerView);
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // Load the video with the specified video ID
                youTubePlayer.loadVideo(uuid, 0);
                youTubePlayer.play();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        musicList = DataCache.getInstance().getSearchCache().getResultList();
        notifyDataSetChanged();
    }
}
