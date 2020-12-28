package com.junipero.capturetheflag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.junipero.capturetheflag.ui.main.SectionsPagerAdapter;

import org.w3c.dom.Text;

public class GameActivity extends AppCompatActivity {

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
        String gameCode = i.getStringExtra("gameCode");
        String role = i.getStringExtra("role");
        String team = i.getStringExtra("team");
        //TextView myLocation = findViewById(R.id....);

        final DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
        /*
        final DatabaseReference myTeamFlagRef = lobby.child(team).child("Keeper");
        final DatabaseReference otherTeamFlagRef = lobby.child((team.equals("Blue") ? "Red" : "Blue" ))
                .child("Keeper");

         */

        //final LocationUpdater myPosition = new LocationUpdater(this, myLocation);


        CoordinatorLayout layout = findViewById(R.id.gameActivityLayout);

        if(team.equals("Red")){
            layout.setBackground(getDrawable(R.drawable.red_bg));
        }else if(team.equals("Blue")){
            layout.setBackground(getDrawable(R.drawable.blue_bg));
        }


    }

}