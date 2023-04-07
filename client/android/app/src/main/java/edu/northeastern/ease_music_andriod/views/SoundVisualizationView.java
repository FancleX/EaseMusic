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

    public SoundVisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize the Paint object to draw the waveform
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5f);
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        // Enable the visualizer when the view is attached to the window
//        visualizer.setEnabled(true);
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        // Disable the visualizer when the view is detached from the window
//        visualizer.setEnabled(false);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the waveform using the Paint object
        if (waveform != null) {
            float width = getWidth();
            float height = getHeight();
            float centerY = height / 2f;
            float step = width / (float) waveform.length;
            float x = 0f;
            float y = centerY - (waveform[0] / 128f) * (height / 2f);
            for (int i = 1; i < waveform.length; i++) {
                float newY = centerY - (waveform[i] / 128f) * (height / 2f);
                canvas.drawLine(x, y, x + step, newY, paint);
                x += step;
                y = newY;
            }
        }
    }


    @Override
    public void onWaveGenerated(byte[] waves) {
        waveform = waves;
        postInvalidate();
    }
}
