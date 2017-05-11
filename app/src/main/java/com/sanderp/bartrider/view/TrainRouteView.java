package com.sanderp.bartrider.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Sander Peerna on 5/7/2017.
 */
public class TrainRouteView extends View {
    private static final String TAG = "TrainRouteView";

    private static final int PAINT_COLOR = Color.BLACK;
    private static final int CIRCLE_RADIUS_DP = 7;
    private static final int CIRCLE_STROKE_DP = 3;
    private static final int LINE_STROKE_DP = 7;

    private Paint circlePaint;
    private Paint linePaint;

    private float width;
    private float height;
    private float circleRadiusPx;
    private float circleStrokePx;
    private float lineStrokePx;

    private int trainRoutes;
    private int[] trainRouteColors;

    public TrainRouteView(Context context) {
        super(context);
        setupPaint();
        trainRoutes = 0;
    }

    public TrainRouteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
        trainRoutes = 0;
    }

    public TrainRouteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupPaint();
        trainRoutes = 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TrainRouteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupPaint();
        trainRoutes = 0;
    }

    private void setupPaint() {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        circleRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_DP, dm);
        circleStrokePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_STROKE_DP, dm);
        lineStrokePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LINE_STROKE_DP, dm);
        height = 2 * circleRadiusPx + circleStrokePx + 1f;

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(PAINT_COLOR);
        circlePaint.setStrokeWidth(circleStrokePx);
        circlePaint.setStrokeJoin(Paint.Join.ROUND);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(PAINT_COLOR);
        linePaint.setStrokeWidth(lineStrokePx);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, (int) height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float shift = circleRadiusPx + circleStrokePx / 2;
        int totalCircles = trainRoutes + 1;
        float circleWidth = 2 * circleRadiusPx + circleStrokePx;
        float lineLength = (width - (totalCircles * circleWidth)) / trainRoutes + shift;
//        Log.d(TAG, "onDraw(): width: " + width + " | lineLength: " + lineLength + " | circleRadius: " + circleRadiusPx  + " | circleStroke: " + circleStrokePx);

        // First draw the lines...
        float from = shift;
        float to = from + lineLength + circleStrokePx / 2;
        for (int route = 0; route < trainRoutes; route++) {
            linePaint.setColor(trainRouteColors[route]);
            canvas.drawLine(from + circleRadiusPx, shift, to, shift, linePaint);
            from += lineLength + shift;
            to = from + lineLength + circleStrokePx / 2;
        }

        // Then draw the circles over the lines...
        if (trainRoutes > 0) {
            from = shift;
            for (int route = 0; route < trainRoutes + 1; route++) {
                canvas.drawCircle(from, shift, circleRadiusPx, circlePaint);
                from += lineLength + shift;
            }
        }
    }

    public void setTrainRoutes(int trainRoutes) {
        this.trainRoutes = trainRoutes;
    }

    public void setTrainRoutes(int trainRoutes, int[] trainRouteColors) {
        this.trainRoutes = trainRoutes;
        this.trainRouteColors = trainRouteColors;
    }
}
