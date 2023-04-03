package edu.northeastern.ease_music_andriod.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SoundVisualizationView extends View {
    private final Paint paint;
    private float[] points;

    public SoundVisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (points != null) {
            canvas.drawLines(points, paint);
        }
    }

    public void setMagnitude(double[] magnitude) {
        points = new float[magnitude.length * 4];
        for (int i = 0; i < magnitude.length; i++) {
            points[i * 4] = getWidth() * i / (float) magnitude.length;
            points[i * 4 + 1] = (float) getHeight() / 2;
            points[i * 4 + 2] = getWidth() * i / (float) magnitude.length;
            points[i * 4 + 3] = (float) getHeight() / 2 - (float) (magnitude[i] * 10);
        }
        invalidate();
    }
}
