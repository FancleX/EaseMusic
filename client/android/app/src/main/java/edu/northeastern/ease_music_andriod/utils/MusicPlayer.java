package edu.northeastern.ease_music_andriod.utils;

import android.media.MediaPlayer;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;

public class MusicPlayer {

    private static volatile MusicPlayer instance;
    private int musicIndex;
    private MusicItem musicItem;
    private byte[] musicBlob;
    private double[] magnitudes;
    private boolean onPlaying;
    private final MediaPlayer mediaPlayer;

    private MusicPlayer() {
        this.mediaPlayer = new MediaPlayer();
    }

    public static MusicPlayer getInstance() {
        if (instance == null) {
            synchronized (MusicPlayer.class) {
                instance = new MusicPlayer();
            }
        }

        return instance;
    }

    public synchronized void setMusicItem(MusicItem musicItem, int position) {
        this.musicItem = musicItem;
        this.musicIndex = position;
    }

    public synchronized void setMusicSource(String src) {
        musicBlob = Base64.getDecoder().decode(src);
        computeMagnitude();
    }

    public String getMusicName() {
        return musicItem.getTitle();
    }

    public String getMusicAuthor() {
        return musicItem.getAuthor();
    }

    public String getMusicAlbumIcon() {
        return musicItem.getThumbnail();
    }

    public String getEstimatePlayTime() {
        return String.format(Locale.US,
                "%02d:%02d",
                TimeUnit.MICROSECONDS.toMinutes(musicBlob.length) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(musicBlob.length) % TimeUnit.MINUTES.toSeconds(1));
    }

    public synchronized void play() {


    }

    public synchronized void pause() {

    }

    public boolean isOnPlaying() {
        return onPlaying;
    }

    private void computeMagnitude() {
        int n = musicBlob.length;
        double[] input = new double[n];

        // convert bytes to doubles
        for (int i = 0; i < n; i++) {
            input[i] = musicBlob[i] / 255.0;
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(input);

        // calculate magnitudes
        magnitudes = new double[n/2+1];
        for (int i = 1; i < n/2; i++) {
            magnitudes[i] = Math.sqrt(input[2*i]*input[2*i] + input[2*i+1]*input[2*i+1]);
        }
    }

}
