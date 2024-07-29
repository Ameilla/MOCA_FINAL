package com.example.moca;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawingView extends View {

    private Bitmap drawingBitmap;
    private Canvas drawingCanvas;
    private Paint paint;
    private Path drawingPath;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

        drawingPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(drawingPath, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                drawingPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                // Optional: Add any additional logic when the touch is released
                break;
        }

        invalidate(); // Trigger redraw
        return true;
    }

    public void clearDrawing() {
        drawingPath.reset();
        invalidate();
    }

    public Bitmap getDrawingBitmap() {
        if (getWidth() > 0 && getHeight() > 0) {
            drawingBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(drawingBitmap);
            draw(tempCanvas);
        }
        return drawingBitmap;
    }
}
