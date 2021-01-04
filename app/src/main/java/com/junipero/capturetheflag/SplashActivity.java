package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        if (sp.getBoolean("isActive", true)){
            startService(new Intent(SplashActivity.this, BackgroundSoundService.class));
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}