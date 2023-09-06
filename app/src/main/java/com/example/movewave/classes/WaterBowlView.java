package com.example.movewave.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.movewave.R;

public class WaterBowlView extends View {
    private Paint wavePaint;

    private Paint circlePaint;

    private Paint textPaint;

    private int screenWidth = 0;

    private int screenHeignt = 0;

    private final int amplitude = 100;
    private Path path;

    private float progress = 0f;
    private float textProgress = 0f;

    private final Point startPoint = new Point();

    public void setProgress(float progress) {
        textProgress = progress;
        if (progress == 100f) {
            this.progress = progress + amplitude;
        } else {
            this.progress = progress;
        }
    }

    private void init() {
        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStrokeWidth(1f);
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(50f);
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#292929"));
        circlePaint.setStrokeWidth(10f);
        circlePaint.setStyle(Paint.Style.STROKE);
    }

    public WaterBowlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public WaterBowlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WaterBowlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterBowlView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(measureSize(400, widthMeasureSpec), measureSize(400, heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeignt = h;
        startPoint.x = -screenWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        clipCircle(canvas);

        drawCircle(canvas);

        drawWave(canvas);

        drawText(canvas);
        postInvalidateDelayed(10);
    }

    private void drawText(Canvas canvas) {
        Rect targetRect = new Rect(0, -screenHeignt, screenWidth, 0);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(textProgress + "%", targetRect.centerX(), baseline, textPaint);
    }

    private void drawWave(Canvas canvas) {
        int height = (int) (progress / 100 * screenHeignt);
        startPoint.y = -height;
        canvas.translate(0f, screenHeignt);
        path = new Path();
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setColor(ContextCompat.getColor(getContext(), R.color.water_blue));
        int wave = screenWidth / 4;
        path.moveTo(startPoint.x, startPoint.y);
        for (int i = 0; i <= 3; i++) {
            int startX = startPoint.x + i * wave * 2;
            int endX = startX + 2 * wave;
            if (i % 2 == 0) {
                path.quadTo((startX + endX) / 2, startPoint.y + amplitude, endX, startPoint.y);
            } else {
                path.quadTo((startX + endX) / 2, startPoint.y - amplitude, endX, startPoint.y);
            }
        }
        path.lineTo(screenWidth, screenHeignt / 2);
        path.lineTo(-screenWidth, screenHeignt / 2);
        path.lineTo(-screenWidth, 0);
        path.close();
        canvas.drawPath(path, wavePaint);
        startPoint.x += 10;
        if (startPoint.x > 0) {
            startPoint.x = -screenWidth;
        }
        path.reset();
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(screenHeignt / 2, screenHeignt / 2, screenHeignt / 2, circlePaint);
    }

    private void clipCircle(Canvas canvas) {
        Path circlePath = new Path();
        circlePath.addCircle(screenWidth / 2, screenHeignt / 2, screenHeignt / 2, Path.Direction.CW);
        canvas.clipPath(circlePath);
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }
        return result;
    }
}

