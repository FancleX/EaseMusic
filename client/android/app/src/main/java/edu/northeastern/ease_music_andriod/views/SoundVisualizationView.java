package edu.northeastern.ease_music_andriod.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

import edu.northeastern.ease_music_andriod.utils.MusicPlayer;

public class SoundVisualizationView extends View implements MusicPlayer.OnWaveGeneratedCallback {
    private final Paint paint;
    private byte[] waveform;
    private String musicUuid;
    private int currentTheme;
    private static final int COLORS = 21;

    private final int[][] themes = {
            {
                    Color.parseColor("#F2F3E2"),
                    Color.parseColor("#ECF2E4"),
                    Color.parseColor("#E4F0E7"),
                    Color.parseColor("#DDEEEA"),
                    Color.parseColor("#D6ECEC"),
                    Color.parseColor("#D0EBEE"),
                    Color.parseColor("#C9EAF0"),
                    Color.parseColor("#C2E9F2"),
                    Color.parseColor("#BDE8F4"),
                    Color.parseColor("#B8E7F6"),
                    Color.parseColor("#B2E5F8"),
                    Color.parseColor("#BADFF7"),
                    Color.parseColor("#C2D9F6"),
                    Color.parseColor("#CAD3F5"),
                    Color.parseColor("#D1CEF4"),
                    Color.parseColor("#D7CAF3"),
                    Color.parseColor("#DEC4F2"),
                    Color.parseColor("#E5BFF1"),
                    Color.parseColor("#EBBAF0"),
                    Color.parseColor("#F0B7F0"),
                    Color.parseColor("#F4B3EF")
            },
            {
                    Color.parseColor("#D7ECBA"),
                    Color.parseColor("#CAF5CF"),
                    Color.parseColor("#D0F1C5"),
                    Color.parseColor("#D5EDBD"),
                    Color.parseColor("#DBE9B4"),
                    Color.parseColor("#E0E6AC"),
                    Color.parseColor("#E6E1A1"),
                    Color.parseColor("#EBDD99"),
                    Color.parseColor("#F1D990"),
                    Color.parseColor("#F4D78B"),
                    Color.parseColor("#F7D486"),
                    Color.parseColor("#F6C985"),
                    Color.parseColor("#F5BE84"),
                    Color.parseColor("#F4B283"),
                    Color.parseColor("#F4A882"),
                    Color.parseColor("#F4A081"),
                    Color.parseColor("#F49780"),
                    Color.parseColor("#F38E7F"),
                    Color.parseColor("#F3867E"),
                    Color.parseColor("#F3807E"),
                    Color.parseColor("#F27A7D")
            },
            {
                    Color.parseColor("#D3EEF4"),
                    Color.parseColor("#D5EEF0"),
                    Color.parseColor("#D8EEEC"),
                    Color.parseColor("#DBEEE8"),
                    Color.parseColor("#DFEEE3"),
                    Color.parseColor("#E3EEDE"),
                    Color.parseColor("#E6EEDA"),
                    Color.parseColor("#E9EED6"),
                    Color.parseColor("#ECEED0"),
                    Color.parseColor("#EFEECC"),
                    Color.parseColor("#F1EEC8"),
                    Color.parseColor("#F1E5BD"),
                    Color.parseColor("#F1DCB2"),
                    Color.parseColor("#F1D5A9"),
                    Color.parseColor("#F1CD9F"),
                    Color.parseColor("#F1C595"),
                    Color.parseColor("#F1BD8B"),
                    Color.parseColor("#F2B682"),
                    Color.parseColor("#F2AE78"),
                    Color.parseColor("#F3A972"),
                    Color.parseColor("#F3A46C")
            },
            {
                    Color.parseColor("#5EDEEF"),
                    Color.parseColor("#6BDFE0"),
                    Color.parseColor("#78E0D1"),
                    Color.parseColor("#82E1C5"),
                    Color.parseColor("#8FE2B5"),
                    Color.parseColor("#9CE3A5"),
                    Color.parseColor("#ABE492"),
                    Color.parseColor("#B7E583"),
                    Color.parseColor("#C6E771"),
                    Color.parseColor("#D1E864"),
                    Color.parseColor("#DCE956"),
                    Color.parseColor("#DEDC65"),
                    Color.parseColor("#DFD56D"),
                    Color.parseColor("#E0CC77"),
                    Color.parseColor("#E2C284"),
                    Color.parseColor("#E4B692"),
                    Color.parseColor("#E5AD9D"),
                    Color.parseColor("#E6A6A6"),
                    Color.parseColor("#E79EAF"),
                    Color.parseColor("#E897B8"),
                    Color.parseColor("#E98FC0")
            }
    };


    public SoundVisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setStrokeWidth(5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the waveform using the Paint object
        if (waveform != null) {
            int n = waveform.length;
            int mod = n / 2 / (COLORS - 1);
            int colorIndex = 0;

            float width = getWidth();
            float height = getHeight();
            float centerY = height / 2f;
            float step = width / (float) n;
            float x = 0f;
            float y = centerY * (1f - waveform[0] / 96f);

            for (int i = 1; i < n; i++) {
                float newY = centerY * (1f - waveform[i] / 96f);

                if (i % mod == mod - 1)
                    colorIndex++;

                int color = colorIndex < COLORS ? themes[currentTheme][colorIndex] : themes[currentTheme][COLORS - 1 - colorIndex % COLORS];
                paint.setColor(color);

                canvas.drawLine(x, y, x + step, newY, paint);
                x += step;
                y = newY;
            }

        }
    }


    @Override
    public void onWaveGenerated(byte[] waves, String uuid) {
        waveform = getMirroredWaves(waves);
        if (!uuid.equals(musicUuid)) {
            musicUuid = uuid;
            currentTheme = new Random().nextInt(4);
        }

        postInvalidate();
    }

    private byte[] getMirroredWaves(byte[] src) {
        int bound = src.length / 2;

        byte[] result = new byte[bound * 2];

        System.arraycopy(src, 0, result, bound, bound);

        for (int i = 0; i < bound; i++) {
            result[bound - i] = src[i];
        }

        return result;
    }
}
