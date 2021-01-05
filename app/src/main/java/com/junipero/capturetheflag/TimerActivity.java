package com.junipero.capturetheflag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
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
    private final String[] data = new String[3];
    private String gameCode;
    // flags to manage background music and the switch between activities
    private boolean isChangingActivity = false;
    private boolean isGoingBackground = false;
    private int numOfPlayers;
    private MediaPlayer tick;   // sound of timer ¯\_(ツ)_/¯

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        final ConstraintLayout layout = findViewById(R.id.timerLayout);

        // stop the music to reproduce sound fx of this activity
        stopService(new Intent(TimerActivity.this, BackgroundSoundService.class));

        // get the lobby code from previous activity
        Intent i = getIntent();
        gameCode = i.getStringExtra("gameCode");
        TextView gameCodeViewer = findViewById(R.id.GameID);
        gameCodeViewer.setText(Html.fromHtml("You are in lobby: <b>" + gameCode + "</b>"));

        // declaring and initializing some views
        final TextView teamViewer = findViewById(R.id.teamView);
        final TextView timerViewer = findViewById(R.id.timeView);

        final DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        final StoredDataManager sdm = new StoredDataManager(TimerActivity.this.getFilesDir());

        // enhance readability over team color's background
        gameCodeViewer.setTextColor(Color.WHITE);
        teamViewer.setTextColor(Color.WHITE);
        timerViewer.setTextColor(Color.WHITE);

        // first data saved will be your actual lobby code
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
                        layout.setBackground(getDrawable(R.drawable.red_bg));
                        data[1] = "Keeper";
                        data[2] = "Red";
                    }
                }
                for (DataSnapshot ds : snapshot.child("Red/Stealer").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team RED, and your role is : Stealer
                        teamViewer.setText(Html.fromHtml("You are in team: <b>Red</b>"));
                        layout.setBackground(getDrawable(R.drawable.red_bg));
                        data[1] = "Stealer";
                        data[2] = "Red";
                    }
                }
                for (DataSnapshot ds : snapshot.child("Blue/Keeper").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team BLUE, and your role is : Keeper
                        teamViewer.setText(Html.fromHtml("You are in team: <b>Blue</b>"));
                        layout.setBackground(getDrawable(R.drawable.blue_bg));
                        data[1] = "Keeper";
                        data[2] = "Blue";
                    }
                }
                for (DataSnapshot ds : snapshot.child("Blue/Stealer").getChildren()){
                    if(ds.getKey().equals(sdm.readID())){
                        // you are in team BLUE, and your role is : Stealer
                        teamViewer.setText(Html.fromHtml("You are in team: <b>Blue</b>"));
                        layout.setBackground(getDrawable(R.drawable.blue_bg));
                        data[1] = "Stealer";
                        data[2] = "Blue";
                    }
                }

                // also get number of players of the actual lobby
                if(snapshot.getValue() != null){
                    numOfPlayers = Integer.parseInt(snapshot.child("Number of players")
                            .getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // initializing and setting parameters of the audio in this activity
        // only if user's audio preferences allow it
        tick = MediaPlayer.create(TimerActivity.this,R.raw.tick);
        tick.setLooping(true);
        tick.setVolume(70,70);
        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        if (!sp.getBoolean("isActive", true)){
            tick.setVolume(0,0);
        }
        tick.start();
        // Countdown 1 minute
        new CountDownTimer(60000, 1000){
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                timerViewer.setText((millisUntilFinished / 1000) + "");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                isChangingActivity = true;
                StoredDataManager sdm = new StoredDataManager(TimerActivity.this.getFilesDir());
                // starts the GameActivity after the countdown, passing the data evaluated before
                Intent i = new Intent(TimerActivity.this, GameActivity.class);
                i.putExtra("gameCode", data[0]);
                i.putExtra("role", data[1]);
                i.putExtra("team", data[2]);
                i.putExtra("id", sdm.getUser().getId());
                i.putExtra("name", sdm.getUser().getName());
                if (!isGoingBackground){
                    startActivity(i);
                }
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
        // if the app is in background stop the music
        tick.stop();
        tick.release();

        // remove myself if the activity is not changing
        if(!isChangingActivity){
            isGoingBackground = true;
            DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);

            // if my role is "Keeper", I need to notify other users that the game has been cancelled
            if(data[1].equals("Keeper")){
                // cancel the game
                lobby.child("State").setValue("Cancelled");
            }
            StoredDataManager sdm = new StoredDataManager(TimerActivity.this.getFilesDir());
            lobby.child(data[2]).child(data[1]).child(sdm.readID()).removeValue();
            numOfPlayers = numOfPlayers -1;
            lobby.child("Number of players").setValue(numOfPlayers);
        }
        // then finish the activity
        finish();
    }
}
