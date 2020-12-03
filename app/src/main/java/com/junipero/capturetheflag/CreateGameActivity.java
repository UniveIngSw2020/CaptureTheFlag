package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class CreateGameActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        TextView test = findViewById(R.id.creategametv);
        test.setText("SONO NELLA CREATE GAME");

        TextView gameId = findViewById(R.id.idgame);
        gameId.setText(gameId.getText() + "\n" + IdGenerator.generateMatchIdTest());
    }
}