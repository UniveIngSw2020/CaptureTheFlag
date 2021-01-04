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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity{

    private static final int STORAGE_PERMISSION_CODE = 101;
    private MediaPlayer player;
    private Intent svc;
    private LocationManager lm;
    private TextView welcome_msg;
    private Button button_create, button_join;
    private boolean isGoingToBackground = true;

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }


    GameDB db = null;
    private final String TAG = "MainActivity";
    private final String Myname = "Nasi";
   // private final String path = MainActivity.this.getFilesDir() + "/userdata/data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome_msg = findViewById(R.id.welcome);

        lm = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);

        /*
        player = MediaPlayer.create(MainActivity.this,R.raw.awesomeness);
        player.setLooping(true);
        player.setVolume(50,50);
        if(!player.isPlaying()){
                player.start();
            }
        }

         */







        /*
        svc = new Intent(this, BackgroundSoundService.class);
        startService(svc);

         */


        //------------------FILE MANAGER-------------------------------
        // check if exists a user created in local storage

        if(!(new File(MainActivity.this.getFilesDir(), "userdata").exists())){
            // need to be refined this piece of code
            startActivity(new Intent(MainActivity.this,
                    CreateUserActivity.class));
        }

        // -------------------BUTTONS-------------------------------

        button_create = findViewById(R.id.button_create);
        /*
        StoredDataManager sdm = new StoredDataManager(MainActivity.this.getFilesDir());
        if(! sdm.getUser().getName().equals("nasik")){
            button_create.setVisibility(View.INVISIBLE);
        }
        */

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGoingToBackground = false;
                startActivity(new Intent(MainActivity.this,
                        CreateGameActivity.class));
            }
        });

        button_join = findViewById(R.id.button_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGoingToBackground = false;
                startActivity(new Intent(MainActivity.this,
                        JoinGameActivity.class));
            }
        });


        // ---------------LOCATION UPDATER-----------------


        checkLocationPermission();

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                    new MyLocationListener());
        } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1,
                    new MyLocationListener());
        }

        // needed to manage location data (only wrapped things)
        //LocationUpdater locationUpdater = new LocationUpdater(this);
        // set the old location saved by the gps in the textView
        //myLocation.setText(locationUpdater.getActualPosition());


        // --------------------------------- GPS enabled? -----------------------------------------

        if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            welcome_msg.setText("Your GPS is turned off.\n\nTurn it on now!");
            // disable buttons
            button_create.setVisibility(View.INVISIBLE);
            button_join.setVisibility(View.INVISIBLE);

            // show location settings page after 2 seconds
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }, 2000);

        }


        // ----------------- DATABASE MANAGER ---------------------------

        db = new GameDB();
       // db.getDbRef().child("/" + Myname);
        db.getDbRef().child("/test").setValue("Sono IN lalalal ");

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        isGoingToBackground = false; // used to not stop the music
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

        SharedPreferences sp = getSharedPreferences("SoundSettings", MODE_PRIVATE);
        if (sp.getBoolean("isActive", true)){
            startService(new Intent(MainActivity.this, BackgroundSoundService.class));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // if the app is in background stop the music
        if(isGoingToBackground){
           stopService(new Intent(MainActivity.this, BackgroundSoundService.class));
        }
        //stopService(svc);
    }



}