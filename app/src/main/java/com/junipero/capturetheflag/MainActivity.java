package com.junipero.capturetheflag;

import java.io.File;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {


    GameDB db = null;
    private final String TAG = "MainActivity";
    private final String Myname = "Nasi";
   // private final String path = MainActivity.this.getFilesDir() + "/userdata/data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //------------------FILE MANAGER-------------------------------
        // check if exists a user created in local storage

        if(!(new File(MainActivity.this.getFilesDir(), "userdata").exists())){
            // need to be refined this piece of code
            startActivity(new Intent(MainActivity.this,
                    CreateUserActivity.class));
        }

        // -------------------BUTTONS-------------------------------


        Button button_create = findViewById(R.id.button_create);
        /*
        StoredDataManager sdm = new StoredDataManager(MainActivity.this.getFilesDir());
        if(! sdm.getUser().getName().equals("nasik")){
            button_create.setVisibility(View.INVISIBLE);
        }
        */

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        CreateGameActivity.class));
            }
        });

        Button button_join = findViewById(R.id.button_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        JoinGameActivity.class));
            }
        });


        // ---------------LOCATION UPDATER-----------------


        checkLocationPermission();

        // needed to manage location data (only wrapped things)
        LocationUpdater locationUpdater = new LocationUpdater(this);
        // set the old location saved by the gps in the textView
        //myLocation.setText(locationUpdater.getActualPosition());



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

}