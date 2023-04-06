package edu.northeastern.ease_music_andriod.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;
import edu.northeastern.ease_music_andriod.views.SoundVisualizationView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MusicFragment extends Fragment {

    // ================ fields ================
    private final MusicPlayer musicPlayer = MusicPlayer.getInstance();
    private final Handler handler = new Handler();
    private static final String TAG = "Music Fragment";
    private final UIUpdater uiUpdater = new UIUpdater();
    private String currentMusicId = "";

    // ================ views ================
    private TextView title, author;
    private ImageView albumCover, albumIcon;
    private ObjectAnimator animator;
    private SoundVisualizationView visualizationView;
    private ImageView shareIcon, addFavoriteIcon, downloadIcon;
    private SeekBar seekBar;
    private TextView currentTime, totalTime;
    private ImageView previousIcon, playIcon, nextIcon;
    private ProgressBar progressIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_music, container, false);

        // initiate views
        title = root.findViewById(R.id.music_player_title);
        author = root.findViewById(R.id.music_player_author);
        albumCover = root.findViewById(R.id.player_album_cover);
        albumIcon = root.findViewById(R.id.player_album_icon);
        visualizationView = root.findViewById(R.id.player_sound_visualization_section);
        shareIcon = root.findViewById(R.id.player_share_icon);
        addFavoriteIcon = root.findViewById(R.id.player_favorite_icon);
        downloadIcon = root.findViewById(R.id.player_download_icon);
        seekBar = root.findViewById(R.id.player_seek_bar);
        currentTime = root.findViewById(R.id.current_play_time);
        totalTime = root.findViewById(R.id.total_play_time);
        previousIcon = root.findViewById(R.id.player_go_previous);
        playIcon = root.findViewById(R.id.player_play_pause);
        nextIcon = root.findViewById(R.id.player_go_next);
        progressIndicator = root.findViewById(R.id.loading_progress);

        // assign values
        title.setText(musicPlayer.getMusicName());
        title.setSelected(true);
        title.setMarqueeRepeatLimit(-1);
        author.setText(musicPlayer.getMusicAuthor());
        Picasso.get()
                .load(musicPlayer.getMusicAlbumIcon())
                .resize(70, 70)
                .transform(new CropCircleTransformation())
                .into(albumIcon);

        animator = ObjectAnimator.ofFloat(albumCover, "rotation", 0f,  360f);
        animator.setDuration(10000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);

        //        visualizationView.setMagnitude(musicPlayer.getMagnitude());

        shareIcon.setOnClickListener(view -> {});
        addFavoriteIcon.setOnClickListener(view -> {});
        downloadIcon.setOnClickListener(view -> {});
        previousIcon.setOnClickListener(view -> musicPlayer.playLastMusic());
        playIcon.setOnClickListener(view -> {
            if (musicPlayer.isPlaying())
                musicPlayer.pause();
            else
                musicPlayer.start();
        });
        nextIcon.setOnClickListener(view -> musicPlayer.playNextMusic());

        progressIndicator.setVisibility(View.VISIBLE);
        currentMusicId = musicPlayer.getMusicUuid();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                    musicPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        requireActivity().runOnUiThread(uiUpdater);

        return root;
    }

    private String formatPlayTime(int length) {
        return String.format(Locale.US,
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1));
    }

    private class UIUpdater implements Runnable {
        @Override
        public void run() {
            if (musicPlayer.isReady()) {
                int position = musicPlayer.getCurrentPosition();
                seekBar.setProgress(position);
                currentTime.setText(formatPlayTime(position));
                progressIndicator.setVisibility(View.INVISIBLE);
                seekBar.setMax(musicPlayer.getDuration());
                totalTime.setText(formatPlayTime(musicPlayer.getDuration()));


                if (musicPlayer.isPlaying()) {

                    if (!currentMusicId.equals(musicPlayer.getMusicUuid())) {
                        currentMusicId = musicPlayer.getMusicUuid();

                        // update display
                        title.setText(musicPlayer.getMusicName());
                        author.setText(musicPlayer.getMusicAuthor());

                        Picasso.get()
                                .load(musicPlayer.getMusicAlbumIcon())
                                .resize(70, 70)
                                .transform(new CropCircleTransformation())
                                .into(albumIcon);
                    }

                    if (animator.isPaused())
                        animator.resume();

                    playIcon.setImageResource(R.drawable.pause_outline_icon_64);
                } else {
                    animator.start();
                    animator.pause();

                    playIcon.setImageResource(R.drawable.play_circle_outline_64);
                }

            } else {
                animator.start();
                animator.pause();
                playIcon.setImageResource(R.drawable.play_circle_outline_64);
                progressIndicator.setVisibility(View.VISIBLE);
            }

            handler.postDelayed(this, 200);
        }
    }


}