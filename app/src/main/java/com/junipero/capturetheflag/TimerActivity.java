package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class TimerActivity extends AppCompatActivity {

    // array containing data of my game (gameCode, my role, my team color)
    final String[] data = new String[3];
    String gameCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        final ConstraintLayout layout = findViewById(R.id.timerLayout);
        Intent i = getIntent();
        gameCode = i.getStringExtra("gameCode");

        TextView gameCodeViewer = findViewById(R.id.GameID);
        gameCodeViewer.setText(Html.fromHtml("You are in lobby: <b>" + gameCode + "</b>"));

        final TextView teamViewer = findViewById(R.id.teamView);
        final TextView timerViewer = findViewById(R.id.timeView);

        final DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        final StoredDataManager sdm = new StoredDataManager(TimerActivity.this.getFilesDir());

        // enhance readability over team color's background
        gameCodeViewer.setTextColor(Color.WHITE);
        teamViewer.setTextColor(Color.WHITE);
        timerViewer.setTextColor(Color.WHITE);


        data[0] = gameCode;


        // get your data from db
        lobby.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // save data of your team and role in the game
                for (DataSnapshot ds : snapshot.child("Red/Keeper").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team RED, and your role is : Keeper
                        teamViewer.setText(Html.fromHtml("You are in team: <b>Red</b>"));
                        //layout.setBackgroundColor(Color.RED);
                        layout.setBackground(getDrawable(R.drawable.red_bg));
                        data[1] = "Keeper";
                        data[2] = "Red";
                    }
                }
                for (DataSnapshot ds : snapshot.child("Red/Stealer").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team RED, and your role is : Stealer
                        teamViewer.setText(Html.fromHtml("You are in team: <b>Red</b>"));
                        //layout.setBackgroundColor(Color.RED);
                        layout.setBackground(getDrawable(R.drawable.red_bg));
                        data[1] = "Stealer";
                        data[2] = "Red";
                    }
                }
                for (DataSnapshot ds : snapshot.child("Blue/Keeper").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team BLUE, and your role is : Keeper
                        teamViewer.setText(Html.fromHtml("You are in team: <b>Blue</b>"));
                        //layout.setBackgroundColor(Color.BLUE);
                        layout.setBackground(getDrawable(R.drawable.blue_bg));
                        data[1] = "Keeper";
                        data[2] = "Blue";
                    }
                }
                for (DataSnapshot ds : snapshot.child("Blue/Stealer").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team BLUE, and your role is : Stealer
                        teamViewer.setText(Html.fromHtml("You are in team: <b>Blue</b>"));
                        //layout.setBackgroundColor(Color.BLUE);
                        layout.setBackground(getDrawable(R.drawable.blue_bg));
                        data[1] = "Stealer";
                        data[2] = "Blue";
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Countdown 1 minute
        new CountDownTimer(5000, 1000){
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                timerViewer.setText((millisUntilFinished / 1000) + "");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                //timerViewer.setText("Let's start!");
                // starts the GameActivity after the countdown
                Intent i = new Intent(TimerActivity.this, GameActivity.class);
                i.putExtra("gameCode", data[0]);
                i.putExtra("role", data[1]);
                i.putExtra("team", data[2]);
                startActivity(i);
                finish();

            }
        }.start();


    }

    // delete all data of current game stored in db
    @Override
    public void onBackPressed() {
        DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        StoredDataManager sdm = new StoredDataManager(TimerActivity.this.getFilesDir());
        // then remove data
        lobby.child(data[2]).child(data[1]).child(sdm.readID()).removeValue();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        StoredDataManager sdm = new StoredDataManager(TimerActivity.this.getFilesDir());
        // then remove data
        lobby.child(data[2]).child(data[1]).child(sdm.readID()).removeValue();
        finish();
    }
}
