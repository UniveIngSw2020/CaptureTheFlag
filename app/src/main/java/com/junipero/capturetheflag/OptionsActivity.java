package com.junipero.capturetheflag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Intent;
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

    View profile, help, settings;
    // views of profile fragment
    TextView idView, nameView, winsView, lostsView, tiesView;

    //views of settings fragment
    SwitchCompat soundToggle;
    EditText changeNicknameEdit;
    Button changeNicknameButton;


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

    // profile fragment views manager
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

    // settings fragment views manager
    private void switch_to_settings(){
        profile.setVisibility(View.INVISIBLE);
        help.setVisibility(View.INVISIBLE);
        settings.setVisibility(View.VISIBLE);
        final StoredDataManager sdm = new StoredDataManager(OptionsActivity.this.getFilesDir());

        soundToggle = findViewById(R.id.soundSwitch);
        changeNicknameEdit = findViewById(R.id.changeNicknameEditText);
        changeNicknameButton = findViewById(R.id.changeNicknameButton);


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

        soundToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // activate sound
                    Toast.makeText(OptionsActivity.this, "SOUND ON", Toast.LENGTH_SHORT)
                            .show();
                }else{
                    // deactivate sound
                    Toast.makeText(OptionsActivity.this, "SOUND OFF", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });



    }

    // --------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

