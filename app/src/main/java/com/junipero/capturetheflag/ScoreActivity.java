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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent i = this.getIntent();
        String team = i.getStringExtra("team");
        String score = i.getStringExtra("score");
        // score contains the message: "Red/Blue wins" or "Tie" or "Cancelled"
        final String gameCode = i.getStringExtra("gameCode");


        StoredDataManager sdm = new StoredDataManager(ScoreActivity.this.getFilesDir());
        TextView scoreText = findViewById(R.id.score);

        if(score.equals("Cancelled")){
            scoreText.setText("The game has been cancelled");
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

        // delete record of the game played from db if still exists
        assert gameCode != null;
        DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        lobby.removeValue();
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
                startActivity(new Intent(ScoreActivity.this,
                        MainActivity.class));
            }
        }, 3000);
    }
}