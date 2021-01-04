package com.junipero.capturetheflag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.junipero.capturetheflag.ui.main.SectionsPagerAdapter;

import org.w3c.dom.Text;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    public SensorManager cSesnor;
    String gameCode, role, team, numOfPlayers;
    private boolean isGoingBack = false;
    private DatabaseReference lobby;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        Intent i = getIntent();
        gameCode = i.getStringExtra("gameCode");
        role = i.getStringExtra("role");
        team = i.getStringExtra("team");
        //TextView myLocation = findViewById(R.id....);


        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        if (sp.getBoolean("isActive", true)){
            startService(new Intent(GameActivity.this, BackgroundSoundService.class));
        }

        lobby = new GameDB().getDbRef().child(gameCode);
        /*
        final DatabaseReference myTeamFlagRef = lobby.child(team).child("Keeper");
        final DatabaseReference otherTeamFlagRef = lobby.child((team.equals("Blue") ? "Red" : "Blue" ))
                .child("Keeper");

         */

        //final LocationUpdater myPosition = new LocationUpdater(this, myLocation);
        lobby.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // state of game controller
                if (snapshot.child("State").getValue() != null) {
                    if (Objects.requireNonNull(snapshot.child("State").getValue()).toString().equals("End")) {
                        endGame(Objects.requireNonNull(snapshot.child("Score").getValue()).toString());
                    }

                    // check if the game has been cancelled
                    else if (Objects.requireNonNull(snapshot.child("State").getValue()).toString().equals("Cancelled")) {
                        endGame("Cancelled");
                    }

                    // Cancel the game if the numbers of player is too low
                    else if (Integer.parseInt(Objects.requireNonNull(snapshot.child("Number of players").getValue()).toString()) < 4) {
                        lobby.child("State").setValue("Cancelled");
                        endGame("Cancelled");
                    }
                }

                // num of players controller
                if (snapshot.child("Number of players").getValue() != null) {
                    numOfPlayers = snapshot.child("Number of players").getValue().toString();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CoordinatorLayout layout = findViewById(R.id.gameActivityLayout);
        // change background color in GameActivity
        if(team.equals("Red")){
            layout.setBackground(getDrawable(R.drawable.red_bg));
        }else if(team.equals("Blue")){
            layout.setBackground(getDrawable(R.drawable.blue_bg));
        }


    }

    private void endGame(String score){
        Intent i = new Intent(GameActivity.this, ScoreActivity.class);
        i.putExtra("score", score);
        i.putExtra("team", team);
        i.putExtra("gameCode", gameCode);
        i.putExtra("numOfPlayers", numOfPlayers);
        i.putExtra("role", role);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isGoingBack = true;
        // if you leave the game your score will be losts +1
        endGame((team.equals("Red")) ? "Blue" : "Red" + " wins");

    }

    @Override
    protected void onPause() {
        super.onPause();
        StoredDataManager sdm = new StoredDataManager(GameActivity.this.getFilesDir());
        lobby.child(team).child(role).child(sdm.getUser().getId()).removeValue();
        // if the app is in background stop the music
        if(!isGoingBack){
            stopService(new Intent(GameActivity.this, BackgroundSoundService.class));
        }
        finish();
    }
}