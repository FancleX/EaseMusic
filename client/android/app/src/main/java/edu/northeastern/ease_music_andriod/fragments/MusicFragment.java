package edu.northeastern.ease_music_andriod.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;
import edu.northeastern.ease_music_andriod.views.SoundVisualizationView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MusicFragment extends Fragment implements View.OnClickListener {

    // ================ fields ================
    private final MusicPlayer musicPlayer = MusicPlayer.getInstance();


    // ================ views ================
    private TextView title, author;
    private ImageView albumCover, albumIcon;
    private Animation rotateAnimation;
    private SoundVisualizationView visualizationView;
    private ImageView shareIcon, addFavoriteIcon, downloadIcon;
    private SeekBar seekBar;
    private TextView currentTime, totalTime;
    private ImageView previousIcon, playIcon, nextIcon;

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

        // assign values
        title.setText(musicPlayer.getMusicName());
        author.setText(musicPlayer.getMusicAuthor());
        Picasso.get()
                .load(musicPlayer.getMusicAlbumIcon())
                .resize(60, 60)
                .transform(new CropCircleTransformation())
                .into(albumIcon);

        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        albumCover.setAnimation(rotateAnimation);
        albumIcon.setAnimation(rotateAnimation);

        totalTime.setText(musicPlayer.getEstimatePlayTime());

        return root;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.player_share_icon) {

        } else if (id == R.id.player_favorite_icon) {

        } else if (id == R.id.player_download_icon) {

        } else if (id == R.id.player_go_previous) {
            playLastMusic();
        } else if (id == R.id.player_play_pause) {
            if (musicPlayer.isOnPlaying())
                pauseMusic();
            else
                playMusic();
        } else if (id == R.id.player_go_next) {
            playNextMusic();
        }

    }

    private void playMusic() {

    }

    private void pauseMusic() {

    }

    private void playNextMusic() {

    }

    private void playLastMusic() {

    }
}