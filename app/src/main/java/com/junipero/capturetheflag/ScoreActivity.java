package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ScoreActivity extends AppCompatActivity {
    private boolean isGoingBack = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent i = this.getIntent();
        String team = i.getStringExtra("team");
        final String role = i.getStringExtra("role");
        String score = i.getStringExtra("score");
        final int numOfPlayers = Integer.parseInt(i.getStringExtra("numOfPlayers"));
        // score contains the message: "Red/Blue wins" or "Tie" or "Cancelled"
        final String gameCode = i.getStringExtra("gameCode");


        StoredDataManager sdm = new StoredDataManager(ScoreActivity.this.getFilesDir());
        TextView scoreText = findViewById(R.id.score);

        if(score.equals("Cancelled")){
            scoreText.setText("The game\nhas been cancelled");
        } else if(score.equals("Tie")) {
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
                finish();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        isGoingBack = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if go back, no need to stop music, else stop
        if(!isGoingBack){
            stopService(new Intent(ScoreActivity.this, BackgroundSoundService.class));
        }
    }
}