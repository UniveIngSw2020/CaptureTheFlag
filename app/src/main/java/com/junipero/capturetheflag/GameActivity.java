package com.junipero.capturetheflag;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.junipero.capturetheflag.ui.main.SectionsPagerAdapter;

import org.w3c.dom.Text;

public class GameActivity extends AppCompatActivity {

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
        final DatabaseReference myTeamFlagRef = lobby.child(team).child("Keeper");
        final DatabaseReference otherTeamFlagRef = lobby.child((team.equals("Blue") ? "Red" : "Blue" ))
                .child("Keeper");



        //final LocationUpdater myPosition = new LocationUpdater(this, myLocation);



    }
}