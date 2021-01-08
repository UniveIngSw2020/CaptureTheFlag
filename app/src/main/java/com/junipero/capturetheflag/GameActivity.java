package com.junipero.capturetheflag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.junipero.capturetheflag.ui.main.SectionsPagerAdapter;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    // declaring some strings used to manage my data
    private String gameCode, role, team, numOfPlayers;
    // flag to manage music on going to background
    private boolean isGoingBack = false;
    private boolean isChangingActivity = false;
    private boolean isGoingBackground = false;
    // reference to the lobby where I'm in
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
        CoordinatorLayout layout = findViewById(R.id.gameActivityLayout);

        // get data from previous activity
        Intent i = getIntent();
        gameCode = i.getStringExtra("gameCode");
        role = i.getStringExtra("role");
        team = i.getStringExtra("team");

        // re-enable background music if the user set it to "Enabled"
        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        if (sp.getBoolean("isActive", true)){
            startService(new Intent(GameActivity.this, BackgroundSoundService.class));
        }

        // initialize the reference to the actual lobby where I'm in
        lobby = new GameDB().getDbRef().child(gameCode);

        lobby.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // state of game controller
                // isGoingBack used to end the game just for a single instance
                if (snapshot.child("State").getValue() != null && !isGoingBack
                        && !isGoingBackground && !isChangingActivity) {

                    // end the actual game if it's ended, then end the game
                    if (Objects.requireNonNull(snapshot.child("State")
                            .getValue()).toString().equals("End")) {
                        endGame(Objects.requireNonNull(snapshot.child("Score")
                                .getValue()).toString());
                    }

                    // check if the game has been cancelled, then end the game
                    else if (Objects.requireNonNull(snapshot.child("State")
                            .getValue()).toString().equals("Cancelled")) {
                        endGame("Cancelled");
                    }

                    // cancel the game if the numbers of player is too low, then and the game
                    else if (Integer.parseInt(Objects.requireNonNull(snapshot.child("Number of players")
                            .getValue()).toString()) < 4) {
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
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // change background color in GameActivity
        if(team.equals("Red")){
            layout.setBackground(getDrawable(R.drawable.red_bg));
        }else if(team.equals("Blue")){
            layout.setBackground(getDrawable(R.drawable.blue_bg));
        }
    }

    // this will switch to another activity to manage the END of the game in different scenarios
    private void endGame(String score){
        isChangingActivity = true;
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        // remove myself from the game if i'm leaving this activity
        StoredDataManager sdm = new StoredDataManager(GameActivity.this.getFilesDir());
        lobby.child(team).child(role).child(sdm.getUser().getId()).removeValue();
        // if the app is in background stop the music
        if(!isGoingBack) {
            stopService(new Intent(GameActivity.this, BackgroundSoundService.class));
        }

        // if you leave the game your score will be losts +1
        if (!isChangingActivity){
            isGoingBackground = true;
            endGame((team.equals("Red")) ? "Blue" : "Red" + " wins");
        }
        // then finish this activity
        finish();
    }
}