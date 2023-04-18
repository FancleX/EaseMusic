package edu.northeastern.ease_music_andriod.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Process;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem.MusicItem;

public class MusicPlayer extends MediaPlayer {

    @SuppressLint("StaticFieldLeak")
    private static volatile MusicPlayer instance;
    private static final String TAG = "Music Player";
    private final APIRequestGenerator requestGenerator = APIRequestGenerator.getInstance();
    private final DataCache dataCache = DataCache.getInstance();
    private CallbackActivity callbackActivity;
    private OnUpdateCallback onUpdateCallback;
    private OnWaveGeneratedCallback onWaveGeneratedCallback;
    private OnDownloadCompletedCallback onDownloadCompletedCallback;

    private Context rootContext;
    private final AtomicBoolean isReady = new AtomicBoolean(false);
    private Visualizer visualizer;
    private Vibrator vibrator;
    private int musicIndex = -1;
    private volatile MusicItem musicItem;
    private byte[] musicBlob;
    private static final String AUDIO_DIR = "AUDIOS";
    private final AtomicBoolean isDownloaded = new AtomicBoolean(false);
    private final AtomicBoolean isOnDownloading = new AtomicBoolean(false);

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
    public void attachRootContext(Context context) {
        rootContext = context;
    }
    public void attachOnDownloadCompletedCallback(OnDownloadCompletedCallback callback) {
        onDownloadCompletedCallback = callback;
    }

    public void enableVibrator() {
        if (vibrator != null)
            return;

        synchronized (this) {
            if (rootContext != null)
                vibrator = (Vibrator) rootContext.getSystemService(Context.VIBRATOR_SERVICE);
        }
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
                        onWaveGeneratedCallback.onWaveGenerated(bytes, getMusicUuid());

                    if (vibrator != null)
                        vibrator.vibrate(getVibrateEffect(bytes));
                }
            }, Visualizer.getMaxCaptureRate(), false, true);
            visualizer.setEnabled(true);
        }
    }

    private void setMusicSource(String src) throws IOException {
        if (musicBlob != null)
            resetPlayer();

        musicBlob = Base64.getDecoder().decode(src);
        MediaDataSource dataSource = new ByteArrayMediaDataSource(musicBlob);
        setDataSource(dataSource);
        prepareAsync();
    }

    private void setMusicSource(byte[] src) throws IOException {
        if (musicBlob != null)
            resetPlayer();

        musicBlob = src;
        isDownloaded.set(true);
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
        isDownloaded.set(false);

        if (hasDownloadedFile(getMusicUuid())) {
            readFromStorage(getMusicUuid());
        }
        else
            sendAccessFileRequest();
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
        isDownloaded.set(false);

        if (onUpdateCallback != null)
            onUpdateCallback.onNext(musicItem.getUuid(), musicIndex);

        if (hasDownloadedFile(getMusicUuid()))
            readFromStorage(getMusicUuid());
        else
            sendAccessFileRequest();
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
        isDownloaded.set(false);

        if (onUpdateCallback != null)
            onUpdateCallback.onLast(musicItem.getUuid(), musicIndex);

        if (hasDownloadedFile(getMusicUuid()))
            readFromStorage(getMusicUuid());
        else
            sendAccessFileRequest();
    }

    private void sendAccessFileRequest() {
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
                    if (callbackActivity != null)
                        callbackActivity.onError(errorMessage);
                }
            });
    }
    private void readFromStorage(String filename) {
        File dir = new File(rootContext.getExternalFilesDir(null), AUDIO_DIR);
        File file = new File(dir, String.format("%s.mp3", filename));

        try {
            setMusicSource(readAllBytes(file));
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            sendAccessFileRequest();
        }
    }

    private byte[] readAllBytes(File file) throws IOException {
        Log.i(TAG, "Read music from: " + file);

        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    private boolean hasDownloadedFile(String filename) {
        File dir = new File(rootContext.getExternalFilesDir(null), AUDIO_DIR);
        if (!dir.exists())
            return false;

        File file = new File(dir, String.format("%s.mp3", filename));
        return file.exists();
    }

    private VibrationEffect getVibrateEffect(byte[] src) {
        int filterFactor = 16;
        int totalAmp = 0;
        int totalCandidates = 0;

        for (byte b : src) {
            int absByte = Math.abs(b);

            if (absByte >= filterFactor) {
                absByte = Math.min(absByte, 127);
                totalAmp += absByte;
                totalCandidates++;
            }
        }

        int averageAmplitude = totalAmp / Math.max(1, totalCandidates) * 2;
        long duration = (long) totalCandidates;

        return VibrationEffect.createOneShot(Math.max(1, duration), Math.max(1, averageAmplitude));
    }

    public void downloadMusic() {
        if (isOnDownloading.get() || isDownloaded.get())
            return;

        File dir = new File(rootContext.getExternalFilesDir(null), AUDIO_DIR);
        if (!dir.exists())
            dir.mkdir();

        File file = new File(dir, String.format("%s.mp3", getMusicUuid()));

        Runnable runnable = () -> {
            isOnDownloading.set(true);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(musicBlob);

                isDownloaded.set(true);
                if (onDownloadCompletedCallback != null)
                    onDownloadCompletedCallback.onSuccess();
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
                isDownloaded.set(false);
                if (onDownloadCompletedCallback != null)
                    onDownloadCompletedCallback.onError(e.getMessage());
            } finally {
                isOnDownloading.set(false);
            }
        };

        new Thread(runnable).start();
    }

    public boolean isDownloaded() {
        return isDownloaded.get();
    }


    public interface CallbackActivity {
        void onError(String error);
    }

    public interface OnUpdateCallback {
        void onNext(String nextMusicId, int nextPosition);
        void onLast(String lastMusicId, int lastPosition);
    }

    public interface OnWaveGeneratedCallback {
        void onWaveGenerated(byte[] waves, String uuid);
    }
    public interface OnDownloadCompletedCallback {
        void onSuccess();
        void onError(String error);
    }
}
