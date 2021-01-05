package com.junipero.capturetheflag;

import java.io.File;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity{

    private LocationManager lm;
    // declaring some views of MainActivity
    private TextView welcome_msg;
    private Button button_create, button_join;
    // flag needed to control if the app is going to background
    private boolean isGoingBack = true;

    // auto-generated class needed for checking permission
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) { }

        @Override
        public void onProviderDisabled(String provider) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome_msg = findViewById(R.id.welcome);



        //------------------FILE MANAGER-------------------------------
        // check if exists a user created in local storage

        if(!(new File(MainActivity.this.getFilesDir(), "userdata").exists())){
            // need to be refined this piece of code
            startActivity(new Intent(MainActivity.this,
                    CreateUserActivity.class));
        }

        // ------------------- BUTTONS SECTION -------------------------------

        button_create = findViewById(R.id.button_create);
        // clicking this button you'll be redirected to CreateGameActivity
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGoingBack = false; // when starting a new activity you're not going to background
                startActivity(new Intent(MainActivity.this,
                        CreateGameActivity.class));
            }
        });

        button_join = findViewById(R.id.button_join);
        // clicking this button you'll be redirected to JoinGameActivity
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGoingBack = false; // when starting a new activity you're not going to background
                startActivity(new Intent(MainActivity.this,
                        JoinGameActivity.class));
            }
        });


        // ---------------LOCATION UPDATER-----------------

        // authorize Location services
        checkLocationPermission();

        // declaring location manager to manage GPS and INTERNET signals
        lm = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                    new MyLocationListener());
        } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1,
                    new MyLocationListener());
        }

        // --------------------------------- GPS enabled? -----------------------------------------

        // managing the app if the GPS is not enabled
        if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            welcome_msg.setText("Your GPS is turned off.\n\nTurn it on now!");
            // hide buttons
            button_create.setVisibility(View.INVISIBLE);
            button_join.setVisibility(View.INVISIBLE);

            // redirect to device's location settings page after 2 seconds
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }, 2000);
        }
    }

    // ------------------------------ END ON CREATE ---------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        // kebab menu options
        // the id is needed switch into another
        switch (id){
            case R.id.action_profile:
                moveToOption(R.id.action_profile);
                return true;
            case R.id.action_help:
                moveToOption(R.id.action_help);
                return true;
            case R.id.action_settings:
                moveToOption(R.id.action_settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // allow to move into a selected option of a kebab menu by a new Intent
    private void moveToOption (int id){
        Intent i = new Intent(MainActivity.this, OptionsActivity.class);
        i.putExtra("option", id);
        isGoingBack = false; // used to not stop the music
        startActivity(i);
    }

    // authorize this app to use Location sensors
    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // needed for permission checkers
        recreate();
        // get the preferences about Sound settings saved in the local storage as a SharePrefs
        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        // start the music if the value is set to TRUE
        if (sp.getBoolean("isActive", true)){
            startService(new Intent(MainActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if the app is in background stop the music
        if(isGoingBack){
           stopService(new Intent(MainActivity.this, BackgroundSoundService.class));
        }
    }



}