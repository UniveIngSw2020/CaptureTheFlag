package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class JoinGameActivity extends AppCompatActivity {

    DatabaseReference db;
    String gameCode = "";
    private boolean isChangingActivity = false;
    private boolean isGoingBackground = false;
    private boolean isGoingBack = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        TextView joinTitle = findViewById(R.id.joingametv);
        joinTitle.setText("JOIN A LOBBY");

        final EditText edit_game = findViewById(R.id.inputidgame);
        final TextView wait_start = findViewById(R.id.waitstart);
        final Button joinButton = findViewById(R.id.joinbutton);
        final TextView labelTitle = findViewById(R.id.inputidgamelabel) ;

        // if not connected to internet return in Main screen
        if(!isNetworkConnected()){
            joinTitle.setText("Internet connection is not enabled");
            joinButton.setEnabled(false);
            labelTitle.setVisibility(View.INVISIBLE);
            edit_game.setVisibility(View.INVISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }

        db = new GameDB().getDbRef();
        final StoredDataManager me = new StoredDataManager(JoinGameActivity.this.getFilesDir());

        // join button event actions
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //checking if the lobby exists and the game is not started yet
                        gameCode = edit_game.getText().toString();
                        if (snapshot.hasChild(gameCode) && snapshot.child(gameCode).child("State")
                                .getValue() != null && snapshot.child(gameCode).child("State")
                                .getValue().toString().equals("Waiting for start")){

                            // the lobby room exists
                            db.child(edit_game.getText().toString()).child("Players")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // if > 10 cannot enter
                                    if(snapshot.getChildrenCount() < 10) {
                                        // inserting my info in players table in the db
                                        db.child(edit_game.getText().toString()).child("Players")
                                                .child(me.readID()).setValue(me.readName());

                                        //players.child(me.readID()).setValue(me.readName());
                                        //write code to perform in db
                                        /*  DEBUG: show players in room
                                        Toast.makeText(JoinGameActivity.this,
                                                Long.valueOf(snapshot.getChildrenCount()).toString() ,
                                                Toast.LENGTH_SHORT).show();
                                         */
                                        joinButton.setAlpha(0.6f);
                                        wait_start.setText("Waiting the game to start...");

                                        // then check if the status changed into "Timer"
                                        db.child(edit_game.getText().toString())
                                                .child("State").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                // here's the checker, then start the Timer activity
                                                if (snapshot.getValue() != null &&
                                                        snapshot.getValue().toString().equals("Timer")){
                                                    // when State is set to TIMER, switch to TimerActivity
                                                    Intent i = new Intent(JoinGameActivity.this,
                                                            TimerActivity.class);
                                                    i.putExtra("gameCode", edit_game.getText().toString());
                                                    isChangingActivity = true;
                                                    if(!isGoingBackground){
                                                        startActivity(i);
                                                    }
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }else{
                                        Toast.makeText(JoinGameActivity.this,
                                                "Room is currently full",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }else{
                            //
                            Toast.makeText(JoinGameActivity.this,
                                    "Room not found ¯\\_(ツ)_/¯ or the game is already started",
                                    Toast.LENGTH_SHORT).show();
                            // the join button will be available for next code of lobby
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

    // internet connection checker
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isGoingBack = true;
        if(!gameCode.equals("")){
            StoredDataManager sdm = new StoredDataManager(JoinGameActivity.this.getFilesDir());
            db.child(gameCode).child("Players").child(sdm.readID()).removeValue();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // if the app is in background stop the music
        if(!isGoingBack){
            stopService(new Intent(JoinGameActivity.this, BackgroundSoundService.class));
        }

        if(!gameCode.equals("") && !isChangingActivity){
            isGoingBackground = true;
            StoredDataManager sdm = new StoredDataManager(JoinGameActivity.this.getFilesDir());
            // remove myself from the lobby
            db.child(gameCode).child("Players").child(sdm.readID()).removeValue();
        }
        finish();
    }
}