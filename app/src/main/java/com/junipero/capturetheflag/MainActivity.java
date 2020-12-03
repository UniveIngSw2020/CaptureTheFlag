package com.junipero.capturetheflag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final String Myname = "Nasi";
   // private final String path = MainActivity.this.getFilesDir() + "/userdata/data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------KEBAB MENU-------------------
        /*
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.new_game:
                    newGame();
                    return true;
                case R.id.help:
                    showHelp();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
         */


        //------------------FILE MANAGER-------------------------------

        //StoredDataManager sdm = new StoredDataManager(MainActivity.this.getFilesDir());

        if(!(new File(MainActivity.this.getFilesDir(), "userdata").exists())){
            // need to be refined this piece of code
            startActivity(new Intent(MainActivity.this,
                    CreateUserActivity.class));
        }
        // DEBUG: show JSON file's content
        /*
        StoredDataManager sdm = new StoredDataManager(MainActivity.this.getFilesDir());
        Toast.makeText(MainActivity.this, sdm.readData(), Toast.LENGTH_LONG)
                .show();

         */




        // -------------------BUTTONS-------------------------------

        Button button_create = findViewById(R.id.button_create);
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
        String latLong;
        TextView myLocation;
        myLocation = findViewById(R.id.myLocation);

        checkLocationPermission();

        // needed to manage location data (only wrapped things)
        LocationUpdater locationUpdater = new LocationUpdater(this, myLocation);
        // set the old location saved by the gps in the textView
        myLocation.setText(locationUpdater.getActualPosition());



        // ----------------- DATABASE MANAGER ---------------------------

        GameDB db = new GameDB();
       // db.getDbRef().child("/" + Myname);
        db.getDbRef().child("/test").setValue("Sono IN lalalal ");


        // not working lol
        /*
        db..setValue("sono IN");
        db.setValue("sono ancora IN");
        db.setValue("sono nuovamente In");
        */

    }

    // ------------------------------ END ON CREATE ---------------------------------------------

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

     */


    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


}