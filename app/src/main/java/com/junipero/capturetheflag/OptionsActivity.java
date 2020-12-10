package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.FrameLayout;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        View profile = findViewById(R.id.fragment_profile);
        View help = findViewById(R.id.fragment_help);
        View settings = findViewById(R.id.fragment_settings);


        Intent i =  getIntent();
        int id = i.getIntExtra("option", 0);

        switch (id) {
            case R.id.action_profile:
                profile.setVisibility(View.VISIBLE);
                help.setVisibility(View.INVISIBLE);
                settings.setVisibility(View.INVISIBLE);
                break;
            case R.id.action_help:
                profile.setVisibility(View.INVISIBLE);
                help.setVisibility(View.VISIBLE);
                settings.setVisibility(View.INVISIBLE);
                break;
            case R.id.action_settings:
                profile.setVisibility(View.INVISIBLE);
                help.setVisibility(View.INVISIBLE);
                settings.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }




    }
}