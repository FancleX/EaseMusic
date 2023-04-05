package edu.northeastern.ease_music_andriod.utils;

import android.media.AudioFocusRequest;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.util.Log;

import org.jtransforms.fft.DoubleFFT_1D;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;

public class MusicPlayer extends MediaPlayer {

    private static volatile MusicPlayer instance;
    private int musicIndex;
    private MusicItem musicItem;
    private byte[] musicBlob;
    private double[] magnitudes;

    private MusicPlayer() {
        super();
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

    public synchronized void setMusicSource(String src) throws IOException {
        musicBlob = Base64.getDecoder().decode(src);
        MediaDataSource dataSource = new ByteArrayMediaDataSource(musicBlob);
        setDataSource(dataSource);
        prepare();
//        Log.i("Music Player", String.valueOf(musicBlob.length));
//        computeMagnitude();
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
        magnitudes[0] = Math.abs(input[0]);
        for (int i = 1; i < n/2; i++) {
            magnitudes[i] = Math.sqrt(input[2*i]*input[2*i] + input[2*i+1]*input[2*i+1]);
        }
        magnitudes[n/2] = Math.abs(input[1]);
    }

    public double[] getMagnitude() {
        return magnitudes;
    }

    public boolean isReady() {
        return musicItem != null && musicBlob != null;
    }
}
