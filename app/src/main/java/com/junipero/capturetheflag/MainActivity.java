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
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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


        //-------------------------------------------------

        //StoredDataManager sdm = new StoredDataManager(path);

        File file = new File(MainActivity.this.getFilesDir(), "userData");
        if (!file.exists()) {
            file.mkdir();
            try {
                File gpxfile = new File(file, "data");
                FileWriter writer = new FileWriter(gpxfile);
                //writer.append("Ciao ho scritto forse la mia prima riga in un file dentro Android");
                writer.write("ID: \nName: \nWins: \nLosts: \nTies: \n" );
                writer.flush();
                writer.close();
                //output.setText(readFile());
                //Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }else{
            File fileEvents = new File(MainActivity.this.getFilesDir() + "/userData/data");
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileEvents));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) { }

            //debug data file ACTIVE THIS WHEN FIXING FILE MANAGER
            //Toast.makeText(MainActivity.this, text.toString(), Toast.LENGTH_LONG).show();
        }








        // -------------------------------------------------
        String latLong;
        TextView myLocation;
        myLocation = findViewById(R.id.myLocation);

        checkLocationPermission();

        // needed to manage location data (only wrapped things)
        LocationUpdater locationUpdater = new LocationUpdater(this, myLocation);
        // set the old location saved by the gps in the textView
        myLocation.setText(locationUpdater.getActualPosition());
        //activate the updates from the locationUpdater
        locationUpdater.getRealTimeUpdates();

        /*
        GameDB db = new GameDB();
       // db.getDbRef().child("/" + Myname);


        db.setValue("sono IN");
        db.setValue("sono ancora IN");
        db.setValue("sono nuovamente In");
        */

    }


    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


}