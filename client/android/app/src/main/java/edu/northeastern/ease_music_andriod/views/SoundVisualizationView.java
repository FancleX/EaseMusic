package edu.northeastern.ease_music_andriod.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import edu.northeastern.ease_music_andriod.utils.MusicPlayer;

public class SoundVisualizationView extends View implements MusicPlayer.OnWaveGeneratedCallback {
    private final Paint paint;
    private byte[] waveform;
    private final int[] colors = {
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
            int mod = n / 2 / (colors.length - 1);
            int colorIndex = 0;

            float width = getWidth();
            float height = getHeight();
            float centerY = height / 2f;
            float step = width / (float) n;
            float x = 0f;
            float y = centerY - (waveform[0] / 64f) * (height / 2f);

            for (int i = 1; i < n; i++) {
                float newY = centerY - (waveform[i] / 64f) * (height / 2f);

                if (i % mod == mod - 1)
                    colorIndex++;

                int color = colorIndex < colors.length ? colors[colorIndex] : colors[colors.length - 1 - colorIndex % colors.length];
                paint.setColor(color);

                canvas.drawLine(x, y, x + step, newY, paint);
                x += step;
                y = newY;
            }

        }
    }


    @Override
    public void onWaveGenerated(byte[] waves) {
        waveform = getMirroredWaves(waves);
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
