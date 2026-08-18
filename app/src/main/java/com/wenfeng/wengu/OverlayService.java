package com.wenfeng.wengu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Overlay service — draws a dynamic colored border on all four screen edges.
 * <p>
 * Uses Beijing Time (UTC+8) as the sole reference, auto-converted worldwide.
 * Peak (Red): Beijing 09:00-12:00, 14:00-18:00
 * Off-peak (Green): all other times
 * <p>
 * Checks current period every 30 seconds and switches color automatically.
 * Supports display on lock screen.
 */
public class OverlayService extends Service {

    public static final String ACTION_START = "com.wenfeng.wengu.START";
    public static final String ACTION_STOP = "com.wenfeng.wengu.STOP";

    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "wenfeng_wengu_overlay";

    private WindowManager windowManager;
    private BorderView borderView;
    private boolean isOverlayAdded = false;

    // Check period every 30 seconds
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable periodCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkAndSetMode();
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(30));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;

        if (ACTION_STOP.equals(action)) {
            stopOverlay();
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
            return START_NOT_STICKY;
        }

        // Start foreground notification (keep-alive) — show on lock screen
        Notification notification = buildNotification("WenFengWenGu is running");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }

        // Add overlay border
        if (!isOverlayAdded) {
            addOverlay();
        }

        // Start periodic check
        handler.post(periodCheckRunnable);

        return START_STICKY; // Auto-restart after being killed
    }

    /**
     * Check if current time is within Beijing peak hours.
     * Peak: 09:00-12:00, 14:00-18:00 (includes 09, excludes 12; includes 14, excludes 18)
     */
    private boolean isPeakHour() {
        // Lock timezone to Beijing UTC+8
        Calendar bjCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        int hour = bjCal.get(Calendar.HOUR_OF_DAY);
        int minute = bjCal.get(Calendar.MINUTE);
        // Convert to minutes for easy comparison
        int totalMin = hour * 60 + minute;

        // 09:00 - 12:00
        boolean morningPeak = totalMin >= 540 && totalMin < 720;
        // 14:00 - 18:00
        boolean afternoonPeak = totalMin >= 840 && totalMin < 1080;

        return morningPeak || afternoonPeak;
    }

    /**
     * Return current period description text
     */
    private String getCurrentPeriodText() {
        return isPeakHour() ? "Peak Hours" : "Off-Peak Hours";
    }

    /**
     * Check current period and set border color
     */
    private void checkAndSetMode() {
        if (borderView == null) return;

        boolean isPeak = isPeakHour();
        int targetMode = isPeak ? BorderView.MODE_PEAK : BorderView.MODE_VALLEY;

        if (borderView.getMode() != targetMode) {
            borderView.setMode(targetMode);
        }

        // Update notification text
        String text = "WenFengWenGu · " + getCurrentPeriodText() + " running";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(NOTIFICATION_ID, buildNotification(text));
        }
    }

    /**
     * Add overlay border
     */
    private void addOverlay() {
        borderView = new BorderView(this);
        borderView.setMode(isPeakHour() ? BorderView.MODE_PEAK : BorderView.MODE_VALLEY);

        // Get screen dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Set LayoutParams: full-screen overlay, transparent background, no touch interception,
        // show on lock screen, turn screen on
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                screenWidth, screenHeight,
                getWindowType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;

        try {
            windowManager.addView(borderView, params);
            isOverlayAdded = true;
            borderView.startPulse();
        } catch (Exception e) {
            // Permission not granted, etc.
            e.printStackTrace();
        }
    }

    /**
     * Select overlay window type by Android version
     */
    private int getWindowType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

    /**
     * Remove overlay
     */
    private void stopOverlay() {
        handler.removeCallbacks(periodCheckRunnable);
        if (borderView != null && isOverlayAdded) {
            try {
                borderView.stopPulse();
                windowManager.removeView(borderView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            borderView = null;
            isOverlayAdded = false;
        }
    }

    // ========== Foreground Notification ==========

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Border Running Status",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows current peak/off-peak period status");
            channel.setShowBadge(false);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification(String contentText) {
        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent stopIntent = new Intent(this, OverlayService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(
                this, 1, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WenFengWenGu")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(contentIntent)
                .addAction(0, "Stop", stopPending)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    @Override
    public void onDestroy() {
        stopOverlay();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
