package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.widget.MediaController;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ScoreActivity extends AppCompatActivity {
    private MediaPlayer scoreSound;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // stop the music to reproduce sound fx of this activity
        stopService(new Intent(ScoreActivity.this, BackgroundSoundService.class));

        Intent i = this.getIntent();
        String team = i.getStringExtra("team");
        final String role = i.getStringExtra("role");
        final String score = i.getStringExtra("score");
        final int numOfPlayers = Integer.parseInt(i.getStringExtra("numOfPlayers"));
        // score contains the message: "Red/Blue wins" or "Tie" or "Cancelled"
        final String gameCode = i.getStringExtra("gameCode");


        StoredDataManager sdm = new StoredDataManager(ScoreActivity.this.getFilesDir());
        TextView scoreText = findViewById(R.id.score);
        if(score.equals("Cancelled")){
            scoreSound = MediaPlayer.create(ScoreActivity.this,R.raw.uauauaaaa);
            scoreText.setText("The game\nhas been cancelled");
        } else if(score.equals("Tie")) {
            scoreSound = MediaPlayer.create(ScoreActivity.this,R.raw.uauauaaaa);
            sdm.increaseTies();
            scoreText.setText("Tie");
            // check if score message contains "myTeamColor wins"  then my team won the game
        } else if(score.equals(team + " wins")){
            scoreSound = MediaPlayer.create(ScoreActivity.this,R.raw.tadaa);
            sdm.increaseWins();
            scoreText.setText("You won");
        } else {
            scoreSound = MediaPlayer.create(ScoreActivity.this,R.raw.uauauaaaa);
            sdm.increaseLosts();
            scoreText.setText("You lost");
        }
        scoreSound.setVolume(50,50);
        scoreSound.start();


        /*
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(gameCode)){
                    for (DataSnapshot game : snapshot.getChildren()){
                        if (game.getKey().equals(gameCode))
                            game.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // delete record of the game played from db if still exists
                assert gameCode != null;
                if(numOfPlayers < 4 || role.equals("Keeper")){
                    DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
                    lobby.removeValue();
                }
                scoreSound.stop();
                scoreSound.release();
                finish();
            }
        }, 4000);
    }

    @Override
    public void onBackPressed() { /* cannot go back */ }

}