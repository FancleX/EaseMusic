package edu.northeastern.ease_music_andriod.fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private ObjectAnimator coverAnimator;
    private ObjectAnimator albumAnimator;
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

        coverAnimator = ObjectAnimator.ofFloat(albumCover, "rotation", 0f,  360f);
        coverAnimator.setDuration(20000);
        coverAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        coverAnimator.setRepeatMode(ObjectAnimator.RESTART);

        albumAnimator = ObjectAnimator.ofFloat(albumIcon, "rotation", 0f, 360f);
        albumAnimator.setDuration(20000);
        albumAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        coverAnimator.setRepeatMode(ObjectAnimator.RESTART);

        // request permissions
        ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
            for (Map.Entry<String, Boolean> result : results.entrySet()) {
                if (result.getKey().equals(Manifest.permission.RECORD_AUDIO))
                    renderVisualizer(root);
                else if (result.getKey().equals(Manifest.permission.VIBRATE))
                    musicPlayer.enableVibrator();
            }
        });

        List<String> permissions = new ArrayList<>();
        if (requireActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.RECORD_AUDIO);
        else
            renderVisualizer(root);

        if (requireActivity().checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.VIBRATE);
        else
            musicPlayer.enableVibrator();

        if (!permissions.isEmpty())
            requestPermissionLauncher.launch(permissions.toArray(new String[0]));

        // add callbacks
        shareIcon.setOnClickListener(view -> shareMusic());
        addFavoriteIcon.setOnClickListener(view -> musicPlayer.addToFavorite());
        downloadIcon.setOnClickListener(view -> musicPlayer.downloadMusic());
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

    private void shareMusic() {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String text = String.format("%s by %s (https://youtu.be/%s)", musicPlayer.getMusicName(), musicPlayer.getMusicAuthor(), musicPlayer.getMusicUuid());
        ClipData clip = ClipData.newPlainText("Ease Music", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), "Share link has copied to your clipboard", Toast.LENGTH_SHORT).show();
    }

    private String formatPlayTime(int length) {
        return String.format(Locale.US,
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(length) % TimeUnit.MINUTES.toSeconds(1));
    }


    private void renderVisualizer(View root) {
        visualizationView = root.findViewById(R.id.player_sound_visualization);
        musicPlayer.enableVisualizer();
        musicPlayer.attachOnWaveGeneratedCallback(visualizationView);
    }

    private void disableIconsClick() {
        shareIcon.setClickable(false);
        addFavoriteIcon.setClickable(false);
        downloadIcon.setClickable(false);

        playIcon.setClickable(false);
        previousIcon.setClickable(false);
        nextIcon.setClickable(false);
    }

    private void enableIconsClick() {
        shareIcon.setClickable(true);
        addFavoriteIcon.setClickable(true);
        downloadIcon.setClickable(true);

        playIcon.setClickable(true);
        previousIcon.setClickable(true);
        nextIcon.setClickable(true);
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
                enableIconsClick();

                if (musicPlayer.isDownloaded()) {
                    downloadIcon.setImageResource(R.drawable.download_done_24);
                    downloadIcon.setClickable(false);
                }

                if (musicPlayer.isFavorite()) {
                    addFavoriteIcon.setImageResource(R.drawable.playlist_remove_24);
                    addFavoriteIcon.setClickable(false);
                }

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

                    if (coverAnimator.isPaused()) {
                        coverAnimator.resume();
                        albumAnimator.resume();
                    } else if (!coverAnimator.isStarted()) {
                        coverAnimator.start();
                        albumAnimator.start();
                    }

                    playIcon.setImageResource(R.drawable.pause_outline_icon_64);
                } else {
                    coverAnimator.start();
                    coverAnimator.pause();
                    albumAnimator.start();
                    albumAnimator.pause();

                    playIcon.setImageResource(R.drawable.play_circle_outline_64);
                }

            } else {
                coverAnimator.start();
                coverAnimator.pause();
                albumAnimator.start();
                albumAnimator.pause();

                playIcon.setImageResource(R.drawable.play_circle_outline_64);
                downloadIcon.setImageResource(R.drawable.download_icon_24);
                addFavoriteIcon.setImageResource(R.drawable.playlist_add_check_24);
                disableIconsClick();

                progressIndicator.setVisibility(View.VISIBLE);
            }

            handler.postDelayed(this, 200);
        }
    }


}