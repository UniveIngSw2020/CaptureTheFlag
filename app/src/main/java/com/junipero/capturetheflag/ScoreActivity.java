package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent i = this.getIntent();
        String team = i.getStringExtra("team");
        String score = i.getStringExtra("score");
        // score contains the message: "Red/Blue wins"

        StoredDataManager sdm = new StoredDataManager(ScoreActivity.this.getFilesDir());
        TextView scoreText = findViewById(R.id.score);

        if(score.equals("Tie")) {
            sdm.increaseTies();
            scoreText.setText("Tie");
            // check if score message contains "myTeamColor wins"  then my team won the game
        } else if(score.equals(team + " wins")){
            sdm.increaseWins();
            scoreText.setText("You won");
        } else {
            sdm.increaseLosts();
            scoreText.setText("You lost");
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ScoreActivity.this,
                        MainActivity.class));
            }
        }, 3000);
    }
}