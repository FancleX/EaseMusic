package edu.northeastern.ease_music_andriod.utils;

import android.media.AudioAttributes;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Process;
import android.util.Log;

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
    private OnUpdateCallback onUpdateCallback;
    private OnWaveGeneratedCallback onWaveGeneratedCallback;
    private final AtomicBoolean isReady = new AtomicBoolean(false);
    private Visualizer visualizer;

    private int musicIndex = -1;
    private MusicItem musicItem;
    private byte[] musicBlob;

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
    public void attachCallbackActivity(CallbackActivity callbackActivity) {
        this.callbackActivity = callbackActivity;
    }
    public void attachOnUpdateCallback(OnUpdateCallback callback) {
        this.onUpdateCallback = callback;
    }
    public void attachOnWaveGeneratedCallback(OnWaveGeneratedCallback callback) {
        this.onWaveGeneratedCallback = callback;
    }

    public void enableVisualizer() {
        if (visualizer != null)
            return;

        synchronized (this) {
            visualizer = new Visualizer(getAudioSessionId());
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {

                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                    if (onWaveGeneratedCallback != null)
                        onWaveGeneratedCallback.onWaveGenerated(bytes);
                }
            }, Visualizer.getMaxCaptureRate(), false, true);
            visualizer.setEnabled(true);
        }
    }

    private synchronized void setMusicSource(String src) throws IOException {
        if (musicBlob != null)
            resetPlayer();

        musicBlob = Base64.getDecoder().decode(src);
        MediaDataSource dataSource = new ByteArrayMediaDataSource(musicBlob);
        setDataSource(dataSource);
        prepareAsync();
    }

    public void requestMusic(MusicItem musicItem, int position) {
        this.musicItem = musicItem;
        musicIndex = position;

        if (musicBlob != null && !isReady.get())
            return;
        if (musicBlob != null && isPlaying())
            pause();

        isReady.set(false);

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

    private void resetPlayer() {
        stop();
        reset();

        musicBlob = null;
    }

    public void playNextMusic() {
        ArrayList<MusicItem> resultList = dataCache.getSearchCache().getResultList();

        if (musicIndex == -1 || musicIndex + 1 >= resultList.size())
            return;

        MusicItem musicItem = resultList.get(musicIndex + 1);
        this.musicItem = musicItem;
        musicIndex++;

        pause();

        isReady.set(false);

        if (onUpdateCallback != null)
            onUpdateCallback.onNext(musicItem.getUuid(), musicIndex);

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
        this.musicItem = musicItem;
        musicIndex--;

        pause();

        isReady.set(false);

        if (onUpdateCallback != null)
            onUpdateCallback.onLast(musicItem.getUuid(), musicIndex);

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

    public interface CallbackActivity {
        void onError(String error);
    }

    public interface OnUpdateCallback {
        void onNext(String nextMusicId, int nextPosition);
        void onLast(String lastMusicId, int lastPosition);
    }

    public interface OnWaveGeneratedCallback {
        void onWaveGenerated(byte[] waves);
    }
}
