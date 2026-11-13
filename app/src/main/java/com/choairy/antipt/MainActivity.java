package com.choairy.antipt;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40,40,40,40);
        root.setBackgroundColor(Color.rgb(18,22,30));
        TextView title = new TextView(this);
        title.setText("AntiPT\n13 não é uma opção.");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        status = new TextView(this);
        status.setTextColor(Color.LTGRAY);
        status.setTextSize(17);
        status.setPadding(0,24,0,24);
        root.addView(status);
        Button start = new Button(this);
        start.setText("Ativar monitoramento");
        start.setOnClickListener(v -> {
            getPreferences(MODE_PRIVATE).edit().putBoolean("enabled", true).apply();
            startForegroundService(new Intent(this, VolumeService.class));
            updateStatus();
        });
        root.addView(start);
        Button stop = new Button(this);
        stop.setText("Desativar monitoramento");
        stop.setOnClickListener(v -> {
            getPreferences(MODE_PRIVATE).edit().putBoolean("enabled", false).apply();
            stopService(new Intent(this, VolumeService.class));
            updateStatus();
        });
        root.addView(stop);
        setContentView(root);
        updateStatus();
    }
    @Override protected void onResume() { super.onResume(); updateStatus(); }
    private void updateStatus() {
        AudioManager audio = (AudioManager)getSystemService(AUDIO_SERVICE);
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        boolean enabled = getPreferences(MODE_PRIVATE).getBoolean("enabled", false);
        status.setText("Monitoramento: " + (enabled ? "ativado" : "desativado") + "\nVolume de mídia: " + volume);
    }
}
