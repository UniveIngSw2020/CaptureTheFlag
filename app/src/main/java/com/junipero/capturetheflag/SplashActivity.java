package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the SharedPrefs about Sound Settings
        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        // if the local value of music is enabled, then start the music
        if (sp.getBoolean("isActive", true)){
            startService(new Intent(SplashActivity.this, BackgroundSoundService.class));
        }
        // then launch the MainActivity
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}