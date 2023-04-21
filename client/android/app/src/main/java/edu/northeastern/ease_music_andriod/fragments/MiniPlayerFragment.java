package edu.northeastern.ease_music_andriod.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MiniPlayerFragment extends Fragment {

    // ================ fields ================
    private final MusicPlayer musicPlayer = MusicPlayer.getInstance();
    private final UIUpdater uiUpdater = new UIUpdater();
    private final Handler handler = new Handler();
    private String currentMusicId = "";


    // ================ views ================
    private ImageView songIcon;
    private TextView songName;
    private ProgressBar progressBar;
    private ImageView playPauseIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_mini_player, container, false);

        songIcon = root.findViewById(R.id.song_icon);
        songName = root.findViewById(R.id.song_name);
        progressBar = root.findViewById(R.id.mini_player_progress_bar);
        playPauseIcon = root.findViewById(R.id.mini_player_play_pause_icon);

        Picasso.get()
                .load(musicPlayer.getMusicAlbumIcon())
                .resize(45, 45)
                .centerCrop()
                .transform(new RoundedCornersTransformation(8, 3))
                .into(songIcon);
        songName.setText(musicPlayer.getMusicName());
        songName.setSelected(true);
        songName.setFocusable(true);
        songName.setFocusableInTouchMode(true);
        songName.setMarqueeRepeatLimit(-1);
        songName.setOnClickListener(view -> showMusicFragment());

        playPauseIcon.setImageResource(R.drawable.play_icon_24);
        playPauseIcon.setOnClickListener(view -> {
            if (!musicPlayer.isReady())
                return;

            if (musicPlayer.isPlaying())
                musicPlayer.pause();
            else
                musicPlayer.start();
        });


        requireActivity().runOnUiThread(uiUpdater);

        return root;
    }

    private void showMusicFragment() {
        BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottom_navigation);
        MenuItem item = navigationView.getMenu().findItem(R.id.music);
        if (item.isChecked())
            return;

        item.setChecked(true);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.frame_layout, new MusicFragment());
        fragmentTransaction.addToBackStack("MusicFragment");

        fragmentTransaction.commit();
    }

    private class UIUpdater implements Runnable {

        @Override
        public void run() {
            if (musicPlayer.isReady()) {
                if (musicPlayer.isPlaying()) {
                    progressBar.setProgress(musicPlayer.getCurrentPosition());

                    if (!currentMusicId.equals(musicPlayer.getMusicUuid())) {
                        currentMusicId = musicPlayer.getMusicUuid();

                        progressBar.setMax(musicPlayer.getDuration());
                        Picasso.get()
                                .load(musicPlayer.getMusicAlbumIcon())
                                .resize(45, 45)
                                .centerCrop()
                                .transform(new RoundedCornersTransformation(8, 3))
                                .into(songIcon);
                        songName.setText(musicPlayer.getMusicName());
                    }

                    playPauseIcon.setImageResource(R.drawable.pause_icon_24);
                } else {
                    playPauseIcon.setImageResource(R.drawable.play_icon_24);
                }
            } else {
                playPauseIcon.setImageResource(R.drawable.play_icon_24);
            }

            handler.postDelayed(this, 250);
        }
    }
}