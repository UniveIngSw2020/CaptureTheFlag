package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import static android.icu.text.DisplayContext.LENGTH_SHORT;

public class OptionsActivity extends AppCompatActivity {
    private boolean isGoingBack = false;

    private View profile, help, settings;
    // views of profile fragment
    private TextView idView, nameView, winsView, lostsView, tiesView;

    //views of settings fragment
    private SwitchCompat soundToggle;
    private EditText changeNicknameEdit;
    private Button changeNicknameButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        profile = findViewById(R.id.fragment_profile);
        help = findViewById(R.id.fragment_help);
        settings = findViewById(R.id.fragment_settings);

        Intent i =  getIntent();    // get intent form previous activity
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

    // profile fragment views manager
    @SuppressLint("SetTextI18n")
    private void switch_to_profile(){
        // set which views are not enabled
        profile.setVisibility(View.VISIBLE);
        help.setVisibility(View.INVISIBLE);
        settings.setVisibility(View.INVISIBLE);
        idView = findViewById(R.id.idView);
        nameView = findViewById(R.id.nicknameView);
        winsView = findViewById(R.id.winsView);
        lostsView = findViewById(R.id.lostsView);
        tiesView = findViewById(R.id.tiesView);

        // obtain my data from the local JSON file
        StoredDataManager sdm = new StoredDataManager(OptionsActivity.this.getFilesDir());
        // then show them
        idView.setText("ID: " + sdm.getUser().getId());
        nameView.setText("Name: " + sdm.getUser().getName());
        winsView.setText("Wins: \n" + sdm.getUser().getWins());
        lostsView.setText("Losts: \n" + sdm.getUser().getLosts());
        tiesView.setText("Ties: \n" + sdm.getUser().getTies());

    }

    private void switch_to_help(){
        // set which views are not enabled
        profile.setVisibility(View.INVISIBLE);
        help.setVisibility(View.VISIBLE);
        settings.setVisibility(View.INVISIBLE);
    }

    // settings fragment views manager
    private void switch_to_settings(){
        // set which views are not enabled
        profile.setVisibility(View.INVISIBLE);
        help.setVisibility(View.INVISIBLE);
        settings.setVisibility(View.VISIBLE);

        // initialize some views about this fragment
        soundToggle = findViewById(R.id.soundSwitch);
        changeNicknameEdit = findViewById(R.id.changeNicknameEditText);
        changeNicknameButton = findViewById(R.id.changeNicknameButton);

        // get my data from local JSON file
        final StoredDataManager sdm = new StoredDataManager(OptionsActivity.this.getFilesDir());

        // listener for button to change your nickname in local JSON file
        changeNicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!changeNicknameEdit.getText().toString().equals("")){
                    sdm.changeUsername(changeNicknameEdit.getText().toString());
                    Toast.makeText(OptionsActivity.this,
                            "Your nickname has been changed", Toast.LENGTH_SHORT).show();
                    changeNicknameEdit.setText("");
                }else{
                    Toast.makeText(OptionsActivity.this,
                            "Please insert a new nickname", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // get the user's preferences about the background music
        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        final SharedPreferences.Editor spEditor = sp.edit();
        soundToggle.setChecked(sp.getBoolean("isActive", true));
        soundToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // activate sound
                    startService(new Intent(OptionsActivity.this, BackgroundSoundService.class));
                    spEditor.putBoolean("isActive", true).apply();
                }else{
                    // deactivate sound
                    stopService(new Intent(OptionsActivity.this, BackgroundSoundService.class));
                    spEditor.putBoolean("isActive", false).apply();
                }
            }
        });



    }

    // --------------------------------------------------------------------------------------------

    @Override
    protected void onRestart() {
        super.onRestart();
        // restart music on resuming the app after going to background
        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        if (sp.getBoolean("isActive", true)){
            startService(new Intent(OptionsActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isGoingBack = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if the app is in background stop the music
        if(!isGoingBack){
            stopService(new Intent(OptionsActivity.this, BackgroundSoundService.class));
        }
    }
}

