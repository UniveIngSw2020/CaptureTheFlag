package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class JoinGameActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        TextView test = findViewById(R.id.joingametv);
        test.setText("SONG NELLA JOIN GAMEEEE");
    }
}