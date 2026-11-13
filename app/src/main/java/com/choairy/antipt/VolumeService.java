package com.choairy.antipt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;

public class VolumeService extends Service {
    private static final String CHANNEL = "antipt_monitor";
    private AudioManager audio;
    private Handler handler;
    private ContentObserver observer;
    private int lastValid = -1;
    private boolean correcting = false;

    @Override public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CHANNEL, "Monitoramento de volume", NotificationManager.IMPORTANCE_LOW));
        Notification notification = new Notification.Builder(this, CHANNEL)
            .setContentTitle("AntiPT ativo")
            .setContentText("Corrigindo o volume 13 automaticamente")
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setOngoing(true).build();
        startForeground(1, notification);
        audio = (AudioManager)getSystemService(AUDIO_SERVICE);
        handler = new Handler(Looper.getMainLooper());
        int current = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (current != 13) lastValid = current;
        observer = new ContentObserver(handler) {
            @Override public void onChange(boolean selfChange) { checkVolume(); }
        };
        getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, observer);
        checkVolume();
    }
    private void checkVolume() {
        if (correcting) return;
        int current = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (current != 13) { lastValid = current; return; }
        int max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (max < 14) return;
        int corrected = lastValid >= 14 ? 12 : (lastValid >= 0 && lastValid < 13 ? 14 : 12);
        correcting = true;
        try {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, corrected, 0);
            lastValid = corrected;
        } catch (SecurityException ignored) {
        } finally {
            correcting = false;
        }
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override public void onDestroy() {
        if (observer != null) getContentResolver().unregisterContentObserver(observer);
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
