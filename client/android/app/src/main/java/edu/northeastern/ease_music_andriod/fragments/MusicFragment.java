package edu.northeastern.ease_music_andriod.fragments;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
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
    private long animationOffsetTime;

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
        author.setText(musicPlayer.getMusicAuthor());
        Picasso.get()
                .load(musicPlayer.getMusicAlbumIcon())
                .resize(65, 65)
                .transform(new CropCircleTransformation())
                .into(albumIcon);

        float albumCoverRotation = albumCover.getRotation();
        animator = ObjectAnimator.ofFloat(albumCover, "rotation", albumCoverRotation, albumCoverRotation + 360f);
        animator.setDuration(10000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);

        //        visualizationView.setMagnitude(musicPlayer.getMagnitude());

        shareIcon.setOnClickListener(view -> {});
        addFavoriteIcon.setOnClickListener(view -> {});
        downloadIcon.setOnClickListener(view -> {});
        previousIcon.setOnClickListener(view -> playLastMusic());
        playIcon.setOnClickListener(view -> {
            if (musicPlayer.isPlaying())
                musicPlayer.pause();
            else
                musicPlayer.start();
        });
        nextIcon.setOnClickListener(view -> playNextMusic());

        progressIndicator.setVisibility(View.VISIBLE);

        musicPlayer.setOnPreparedListener(mediaPlayer -> {
            progressIndicator.setVisibility(View.GONE);

            totalTime.setText(formatPlayTime(musicPlayer.getDuration()));

            playMusic();

            requireActivity().runOnUiThread(uiUpdater);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        musicPlayer.seekTo(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        });

        return root;
    }

    private void playMusic() {
        // play animation and show pause icon
        animator.start();
        playIcon.setImageResource(R.drawable.pause_outline_icon_64);
        seekBar.setProgress(0);
        seekBar.setMax(musicPlayer.getDuration());

        musicPlayer.start();
    }

    private void pauseMusic() {
        musicPlayer.pause();
    }

    private void resumeMusic() {
        musicPlayer.start();
    }

    private void playNextMusic() {

    }

    private void playLastMusic() {

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
            if (musicPlayer.isPlaying()) {
                int position = musicPlayer.getCurrentPosition();
                seekBar.setProgress(position);
                currentTime.setText(formatPlayTime(position));

                if (albumCover.getAnimation() == null)
                    animator.resume();

                playIcon.setImageResource(R.drawable.pause_outline_icon_64);
            } else {
                animator.pause();
                playIcon.setImageResource(R.drawable.play_circle_outline_64);
            }

            handler.postDelayed(this, 100);
        }
    }

}