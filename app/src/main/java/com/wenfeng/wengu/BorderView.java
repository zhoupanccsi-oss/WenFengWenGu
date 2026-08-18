package com.wenfeng.wengu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Border view — draws dynamic gradient color bars on all four screen edges.
 * <p>
 * Peak (red): red gradient + breathing animation (bright→dim→bright loop)
 * Off-peak (green): green gradient + breathing animation
 * <p>
 * Border thickness ~8dp, center is transparent, does not affect normal phone usage.
 */
public class BorderView extends View {

    public static final int MODE_PEAK = 1;   // Peak
    public static final int MODE_VALLEY = 2; // Off-peak

    private int currentMode = MODE_PEAK;
    private float borderThickness; // px
    private float cornerRadius;

    private Paint borderPaint;
    private ValueAnimator pulseAnimator;

    // Breathing animation current alpha (0.3 ~ 1.0)
    private float currentAlpha = 1.0f;

    public BorderView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Border thickness 8dp to px
        borderThickness = dpToPx(context, 8);
        cornerRadius = dpToPx(context, 24); // Rounded corners

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.FILL);

        // Breathing animation: alpha cycles between 0.3~1.0
        pulseAnimator = ValueAnimator.ofFloat(0.3f, 1.0f);
        pulseAnimator.setDuration(2000); // 2-second cycle
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimator.addUpdateListener(animation -> {
            currentAlpha = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    public void setMode(int mode) {
        if (mode == currentMode) return;
        currentMode = mode;
        invalidate();
    }

    public int getMode() {
        return currentMode;
    }

    public void startPulse() {
        if (pulseAnimator != null && !pulseAnimator.isStarted()) {
            pulseAnimator.start();
        } else if (pulseAnimator != null && !pulseAnimator.isRunning()) {
            pulseAnimator.resume();
        }
    }

    public void stopPulse() {
        if (pulseAnimator != null && pulseAnimator.isRunning()) {
            pulseAnimator.pause();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int baseColor = (currentMode == MODE_PEAK) ? Color.RED : Color.GREEN;

        int alpha = Math.round(currentAlpha * 255);
        int r = Color.red(baseColor);
        int g = Color.green(baseColor);
        int b = Color.blue(baseColor);

        int solid = Color.argb(alpha, r, g, b);
        int faded = Color.argb(Math.round(currentAlpha * 60), r, g, b);

        // Top gradient
        Shader topShader = new LinearGradient(0, 0, w, 0, new int[]{faded, solid, faded}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP);
        borderPaint.setShader(topShader);
        borderPaint.setColor(solid);
        borderPaint.setShader(topShader);
        canvas.drawRoundRect(0, 0, w, borderThickness, cornerRadius, cornerRadius, borderPaint);

        // Bottom gradient
        Shader bottomShader = new LinearGradient(0, 0, w, 0, new int[]{faded, solid, faded}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP);
        borderPaint.setShader(bottomShader);
        borderPaint.setColor(solid);
        borderPaint.setShader(bottomShader);
        canvas.drawRoundRect(0, h - borderThickness, w, h, cornerRadius, cornerRadius, borderPaint);

        // Left gradient
        Shader leftShader = new LinearGradient(0, 0, 0, h, new int[]{faded, solid, faded}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP);
        borderPaint.setShader(leftShader);
        borderPaint.setColor(solid);
        borderPaint.setShader(leftShader);
        canvas.drawRoundRect(0, 0, borderThickness, h, cornerRadius, cornerRadius, borderPaint);

        // Right gradient
        Shader rightShader = new LinearGradient(0, 0, 0, h, new int[]{faded, solid, faded}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP);
        borderPaint.setShader(rightShader);
        borderPaint.setColor(solid);
        borderPaint.setShader(rightShader);
        canvas.drawRoundRect(w - borderThickness, 0, w, h, cornerRadius, cornerRadius, borderPaint);

        // Clear shader to avoid affecting other drawing
        borderPaint.setShader(null);
    }

    private float dpToPx(Context context, float dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return dp * metrics.density;
    }
}
