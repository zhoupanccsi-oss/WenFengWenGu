package com.wenfeng.wengu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Main screen — permission request + start/stop service + real-time status display
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private TextView tvPeriod;
    private Button btnToggle;
    private boolean isRunning = false;

    // Refresh period info in real time
    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            updatePeriodInfo();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvPeriod = findViewById(R.id.tvPeriod);
        btnToggle = findViewById(R.id.btnToggle);

        btnToggle.setOnClickListener(v -> {
            if (isRunning) {
                stopService();
            } else {
                requestPermissionsAndStart();
            }
        });

        // Donate button
        Button btnDonate = findViewById(R.id.btnDonate);
        btnDonate.setOnClickListener(v -> showDonateDialog());

        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }
    }

    /**
     * Show donation QR code dialog (WeChat Pay)
     */
    private void showDonateDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_donate, null);
        ImageView qrImage = dialogView.findViewById(R.id.imgDonateQr);
        qrImage.setImageResource(R.drawable.donate_qr);

        new AlertDialog.Builder(this)
                .setTitle("Donate 0.05 RMB")
                .setView(dialogView)
                .setMessage("Scan via WeChat Pay")
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }

    private void requestPermissionsAndStart() {
        // 1. Check overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Open overlay permission settings page
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, 100);
            Toast.makeText(this, "Please grant \"Display over other apps\" permission, then tap Start again", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Permission ready, start service
        startOverlayService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startOverlayService();
            } else {
                Toast.makeText(this, "Overlay permission not granted, cannot start", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startOverlayService() {
        Intent intent = new Intent(this, OverlayService.class);
        intent.setAction(OverlayService.ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        isRunning = true;
        updateUI();
        Toast.makeText(this, "Border started", Toast.LENGTH_SHORT).show();
    }

    private void stopService() {
        Intent intent = new Intent(this, OverlayService.class);
        intent.setAction(OverlayService.ACTION_STOP);
        startService(intent);
        isRunning = false;
        updateUI();
        Toast.makeText(this, "Border stopped", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        if (isRunning) {
            tvStatus.setText("Running");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnToggle.setText("Stop Border");
        } else {
            tvStatus.setText("Stopped");
            tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
            btnToggle.setText("Start Border");
        }
    }

    /**
     * Update current Beijing time and peak/off-peak period in real time
     */
    private void updatePeriodInfo() {
        Calendar bjCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        int hour = bjCal.get(Calendar.HOUR_OF_DAY);
        int minute = bjCal.get(Calendar.MINUTE);
        int totalMin = hour * 60 + minute;

        boolean isPeak = (totalMin >= 540 && totalMin < 720) || (totalMin >= 840 && totalMin < 1080);

        String timeStr = String.format("%02d:%02d", hour, minute);
        String periodStr = isPeak ? "Peak Hours" : "Off-Peak Hours";
        String priceHint = isPeak ? "Full Price" : "50% of Peak Price";

        tvPeriod.setText(String.format(
                "Beijing Time %s\nCurrent: %s\nPrice: %s",
                timeStr, periodStr, priceHint
        ));

        // Peak = red, Off-peak = green
        int color = isPeak
                ? getResources().getColor(android.R.color.holo_red_dark)
                : getResources().getColor(android.R.color.holo_green_dark);
        tvPeriod.setTextColor(color);
    }

    // ========== Notification Permission (Android 13+) ==========

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                // Whether granted or not, core functionality is unaffected
            });

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
