package edu.northeastern.ease_music_andriod.recyclerViewComponents.musicItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.fragments.SearchFragment;
import edu.northeastern.ease_music_andriod.utils.DataCache;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemViewHolder> implements MusicItemViewHolder.OnMusicItemClickListener {

    private ArrayList<MusicItem> musicList;
    private final SearchFragment mainFragment;

    public MusicItemAdapter(SearchFragment mainFragment) {
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
        holder.bindData(musicList.get(position), position);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return musicList == null ? 0 : musicList.size();
    }


    @Override
    public void onTextClick(View view, int position) {
        MusicItem musicItem = musicList.get(position);
        mainFragment.requestAudioResource(musicItem, position);
    }

    @Override
    public void onBrowserIconClick(View view, int position) {
        // pause music player
        MusicPlayer instance = MusicPlayer.getInstance();
        if (instance.isReady() && instance.isPlaying())
            instance.pause();

        MusicItem musicItem = musicList.get(position);
        String uuid = musicItem.getUuid();

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
