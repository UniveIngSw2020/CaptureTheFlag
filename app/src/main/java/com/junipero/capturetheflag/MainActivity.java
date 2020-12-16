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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    Sensor mSensorAccelerometer;
    Sensor mSensorMagnetometer;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private Display mDisplay;
    TextView azimuthText;

    private final String TAG = "MainActivity";
    private final String Myname = "Nasi";
   // private final String path = MainActivity.this.getFilesDir() + "/userdata/data";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        azimuthText = findViewById(R.id.azimuth);


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





/*
        float [] R = new float[9];
        float [] values = new float[3];
        SensorManager.getOrientation(R, values);
        */


















        // ----------------- DATABASE MANAGER ---------------------------

        GameDB db = new GameDB();
       // db.getDbRef().child("/" + Myname);
        db.getDbRef().child("/test").setValue("Sono IN lalalal ");

    }

    // ------------------------------ END ON CREATE ---------------------------------------------

    // -----------------------------  -----------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();

        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // The sensor type (as defined in the Sensor class).
        int sensorType = sensorEvent.sensor.getType();

        // The sensorEvent object is reused across calls to onSensorChanged().
        // clone() gets a copy so the data doesn't change out from under us
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }
        // Compute the rotation matrix: merges and translates the data
        // from the accelerometer and magnetometer, in the device coordinate
        // system, into a matrix in the world's coordinate system.
        //
        // The second argument is an inclination matrix, which isn't
        // used in this example.
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);


        // Get the orientation of the device (azimuth, pitch, roll) based
        // on the rotation matrix. Output units are radians.
        float orientationValues[] = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix,
                    orientationValues);
        }

        // Pull out the individual values from the array.
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];


        // Fill in the string placeholders and set the textview text.
        // Fill in the string placeholders and set the textview text.
        //azimuth = (azimuth + 0 + 360) % 360;
        azimuth = (azimuth < 0) ? (float) (2 * Math.PI + azimuth) : azimuth;
        azimuthText.setText(Math.toDegrees(azimuth) + "");

    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    // ------------------------------------------------------------------------------------------
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

        // kebab menu options
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

    private void moveToOption (int id){
        Intent i = new Intent(MainActivity.this, OptionsActivity.class);
        i.putExtra("option", id);
        startActivity(i);
    }

    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

}