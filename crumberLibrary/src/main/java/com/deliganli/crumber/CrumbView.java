package com.deliganli.crumber;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

/**
 * A view to draw lines made by dots.
 */
public class CrumbView extends View {
    private int dotInterval;
    private int dotRadius;

    private Paint defaultPaint = new Paint();
    private float[] pathArray = new float[0];

    public CrumbView(Context context) {
        this(context, null);
    }

    public CrumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        defaultPaint.setColor(fetchDefaultColour());

        int densityRatio = getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        dotInterval = 16 * densityRatio;
        dotRadius = 4 * densityRatio;
    }

    /**
     * Gets the accent colour for default
     *
     * @return default colour
     */
    protected int fetchDefaultColour() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public int getDotInterval() {
        return dotInterval;
    }

    /**
     * Set dot interval from one center of the dot to another in pixels
     *
     * @param dotInterval interval in px
     */
    public void setDotInterval(int dotInterval) {
        this.dotInterval = dotInterval;
    }

    public int getDotRadius() {
        return dotRadius;
    }

    /**
     * Sets dot radius in px
     *
     * @param dotRadius radius in px
     */
    public void setDotRadius(int dotRadius) {
        this.dotRadius = dotRadius;
    }

    /**
     * Concats and draws the given x,y pairs to screen
     *
     * @param points nodes which will be concatenate. Points are relative to the view and values between [0,1]
     */
    public void setPath(List<PointF> points) {
        pathArray = createPathArray(points);
        invalidate();
    }

    /**
     * Sets and draws a path array.
     * <p>
     * <br>
     * pathArray parameter should be like below for each line;<br>
     * <p>
     * pathArray[i] = startX<br>
     * pathArray[i+1] = endX<br>
     * pathArray[i+2] = startY<br>
     * pathArray[i+3] = endY<br>
     * </p>
     *
     * @param pathArray nodes which will be concatenate. Points are relative to the view and values between [0,1].
     */
    public void setPathArray(float[] pathArray) {
        this.pathArray = pathArray;
        invalidate();
    }

    /**
     * Fills the given path array with dots
     *
     * @param canvas to draw on
     * @param path   lines that will be filled with dots
     */
    protected void drawPath(Canvas canvas, float[] path) {
        float lastHypotenuse = dotInterval;
        for (int i = 0; i < path.length; i += 4) {
            float startX = path[i];
            float endX = path[i + 2];

            float startY = path[i + 1];
            float endY = path[i + 3];

            float leftoverLength = dotInterval - lastHypotenuse;
            float hypotenuse = (float) Math.hypot(startX - endX, startY - endY);
            if (hypotenuse < leftoverLength) {
                lastHypotenuse += hypotenuse;
                continue;
            }

            float angle = (float) Math.atan2(endY - startY, endX - startX);
            float cosAngle = (float) Math.cos(angle);
            float sinAngle = (float) Math.sin(angle);

            float incrementationX = cosAngle * leftoverLength;
            float incrementationY = sinAngle * leftoverLength;
            startX += incrementationX;
            startY += incrementationY;
            canvas.drawCircle(startX, startY, dotRadius, defaultPaint);

            incrementationX = cosAngle * dotInterval;
            incrementationY = sinAngle * dotInterval;

            while ((lastHypotenuse = (float) Math.hypot(startX - endX, startY - endY)) > dotInterval) {
                startX += incrementationX;
                startY += incrementationY;
                canvas.drawCircle(startX, startY, dotRadius, defaultPaint);
            }
        }
    }

    /**
     * Creates a path array to be used to draw lines
     *
     * @param points nodes which will be concatenate. Points are relative to the view and values between [0,1]
     * @return float array that can be used on both drawing straight or dotted lines
     */
    protected float[] createPathArray(List<PointF> points) {
        float[] pathArray = new float[(points.size() - 1) * 4];
        for (int i = 0; i < points.size() - 1; i++) {
            int index = i * 4;

            PointF from = points.get(i);
            pathArray[index] = translateX(from.x);
            pathArray[index + 1] = translateY(from.y);

            PointF to = points.get(i + 1);
            pathArray[index + 2] = translateX(to.x);
            pathArray[index + 3] = translateY(to.y);
        }
        return pathArray;
    }

    private int translateX(float x) {
        return (int) (x * getWidth());
    }

    private int translateY(float y) {
        return (int) (y * getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPath(canvas, pathArray);
        super.onDraw(canvas);
    }
}
