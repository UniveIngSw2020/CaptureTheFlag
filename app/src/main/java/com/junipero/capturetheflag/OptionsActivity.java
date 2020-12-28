package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OptionsActivity extends AppCompatActivity {

    View profile, help, settings;
    TextView idView, nameView, winsView, lostsView, tiesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        profile = findViewById(R.id.fragment_profile);
        help = findViewById(R.id.fragment_help);
        settings = findViewById(R.id.fragment_settings);

        Intent i =  getIntent();
        int id = i.getIntExtra("option", 0);

        switch (id) {
            case R.id.action_profile:
                switch_to_profile();
                break;
            case R.id.action_help:
                switch_to_help();
                break;
            case R.id.action_settings:
                switch_to_settings();
                break;
            default:
                break;
        }



    }

    @SuppressLint("SetTextI18n")
    private void switch_to_profile(){
        profile.setVisibility(View.VISIBLE);
        help.setVisibility(View.INVISIBLE);
        settings.setVisibility(View.INVISIBLE);
        idView = findViewById(R.id.idView);
        nameView = findViewById(R.id.nicknameView);
        winsView = findViewById(R.id.winsView);
        lostsView = findViewById(R.id.lostsView);
        tiesView = findViewById(R.id.tiesView);

        StoredDataManager sdm = new StoredDataManager(OptionsActivity.this.getFilesDir());

        idView.setText("ID: " + sdm.getUser().getId());
        nameView.setText("Name: " + sdm.getUser().getName());
        winsView.setText("Wins: \n" + sdm.getUser().getWins());
        lostsView.setText("Losts: \n" + sdm.getUser().getLosts());
        tiesView.setText("Ties: \n" + sdm.getUser().getTies());

    }

    private void switch_to_help(){
        profile.setVisibility(View.INVISIBLE);
        help.setVisibility(View.VISIBLE);
        settings.setVisibility(View.INVISIBLE);
    }

    private void switch_to_settings(){
        profile.setVisibility(View.INVISIBLE);
        help.setVisibility(View.INVISIBLE);
        settings.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

