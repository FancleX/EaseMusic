package edu.northeastern.ease_music_andriod.utils;

import android.media.AudioAttributes;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jtransforms.fft.DoubleFFT_1D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;

public class MusicPlayer extends MediaPlayer {

    private static volatile MusicPlayer instance;
    private static final String TAG = "Music Player";
    private final APIRequestGenerator requestGenerator = APIRequestGenerator.getInstance();
    private final DataCache dataCache = DataCache.getInstance();
    private CallbackActivity callbackActivity;
    private final AtomicBoolean isReady = new AtomicBoolean(false);
    private OnNextCallback onNextCallback;

    private int musicIndex = -1;
    private MusicItem musicItem;
    private byte[] musicBlob;
    private double[] magnitudes;

    private MusicPlayer() {
        super();
        setOnCompletionListener(mediaPlayer -> playNextMusic());
        setOnPreparedListener(mediaPlayer -> {
            isReady.set(true);
            start();
        });

        setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
        );

        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
    }

    public static MusicPlayer getInstance() {
        if (instance == null) {
            synchronized (MusicPlayer.class) {
                instance = new MusicPlayer();
            }
        }

        return instance;
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
    public int getMusicIndex() {
        return musicIndex;
    }
    public String getMusicUuid() {
        return musicItem.getUuid();
    }
    public boolean isReady() {
        return isReady.get();
    }

    private synchronized void setMusicSource(String src) throws IOException {
        if (musicBlob != null)
            resetPlayer();

        isReady.set(false);
        musicBlob = Base64.getDecoder().decode(src);
        MediaDataSource dataSource = new ByteArrayMediaDataSource(musicBlob);
        setDataSource(dataSource);
        prepareAsync();
//        Log.i("Music Player", String.valueOf(musicBlob.length));
//        computeMagnitude();
    }

    public void requestMusic(MusicItem musicItem, int position) {
        this.musicItem = musicItem;
        musicIndex = position;

        if (musicBlob != null && !isReady.get())
            return;
        if (musicBlob != null && isPlaying())
            pause();

        requestGenerator.accessResource(musicItem.getUuid(), new APIRequestGenerator.RequestCallback() {
            @Override
            public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                try {
                    String encodedAudioFile = response.getString("data");

                    setMusicSource(encodedAudioFile);
                } catch (JSONException | IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage, RequestAPIs.APILabel label) {
                Log.e(TAG, label.toString() + ": " + errorMessage);
                callbackActivity.onError(errorMessage);
            }
        });
    }



    public void attachCallbackActivity(CallbackActivity callbackActivity) {
        this.callbackActivity = callbackActivity;
    }

    private void resetPlayer() {
        stop();
        reset();

        musicBlob = null;
        magnitudes = null;
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


    public void playNextMusic() {
        ArrayList<MusicItem> resultList = dataCache.getSearchCache().getResultList();

        if (musicIndex == -1 || musicIndex + 1 >= resultList.size())
            return;

        MusicItem musicItem = resultList.get(musicIndex + 1);
        this.musicItem = musicItem;
        musicIndex++;

        pause();

        onNextCallback.onNext(musicItem.getUuid(), musicIndex);

        requestGenerator.accessResource(musicItem.getUuid(), new APIRequestGenerator.RequestCallback() {
            @Override
            public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                try {
                    String encodedAudioFile = response.getString("data");

                    setMusicSource(encodedAudioFile);
                } catch (JSONException | IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage, RequestAPIs.APILabel label) {
                Log.e(TAG, label.toString() + ": " + errorMessage);
                callbackActivity.onError(errorMessage);
            }
        });
    }

    public void playLastMusic() {
        ArrayList<MusicItem> resultList = dataCache.getSearchCache().getResultList();

        if (musicIndex == -1 || musicIndex - 1 < 0)
            return;

        MusicItem musicItem = resultList.get(musicIndex - 1);
        musicIndex--;

        pause();
        requestGenerator.accessResource(musicItem.getUuid(), new APIRequestGenerator.RequestCallback() {
            @Override
            public void onSuccess(JSONObject response, RequestAPIs.APILabel label) {
                try {
                    String encodedAudioFile = response.getString("data");

                    setMusicSource(encodedAudioFile);
                } catch (JSONException | IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage, RequestAPIs.APILabel label) {
                Log.e(TAG, label.toString() + ": " + errorMessage);
                callbackActivity.onError(errorMessage);
            }
        });
    }

    public void attachOnNextCallback(OnNextCallback callback) {
        this.onNextCallback = callback;
    }

    public interface CallbackActivity {
        void onError(String error);
    }

    public interface OnNextCallback {
        void onNext(String nextMusicId, int nextPosition);
    }
}
