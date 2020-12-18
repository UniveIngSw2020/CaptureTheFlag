package com.junipero.capturetheflag.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.junipero.capturetheflag.CTFCriteria;
import com.junipero.capturetheflag.GameDB;
import com.junipero.capturetheflag.LocationUpdater;
import com.junipero.capturetheflag.MainActivity;
import com.junipero.capturetheflag.R;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    /*
    private SensorManager mSensorManager;
    Sensor mSensorAccelerometer;
    Sensor mSensorMagnetometer;


    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    TextView azimuthText;
    GameDB db;
    double azimuthDeg;

     */

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

    }

    /*
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = null;
        //final TextView textView = root.findViewById(R.id.section_label);
        switch (getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                root = inflater.inflate(R.layout.fragment_main, container, false);
                break;
            case 2:
                root = inflater.inflate(R.layout.fragment_chat, container, false);
                break;
        }


        return root;
    }
    */

    /*

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        switch (getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                // code for GAME section
                Intent i = this.getActivity().getIntent();
                String gameCode = i.getStringExtra("gameCode");
                String role = i.getStringExtra("role");
                String team = i.getStringExtra("team");

                final DatabaseReference lobby = new GameDB().getDbRef().child(gameCode);
                final DatabaseReference myTeamFlagRef = lobby.child(team).child("Keeper");
                final DatabaseReference otherTeamFlagRef = lobby.child((team.equals("Blue") ? "Red" : "Blue" ))
                        .child("Keeper");

                TextView degreeFromOtherView = view.findViewById(R.id.degreeFromOther);
                TextView degreeFromMyTeamFlagView = view.findViewById(R.id.degreeFromMyTeam);
                TextView distanceFromOtherView = view.findViewById(R.id.distanceFromOther);
                TextView distanceFromMyTeamFlagView = view.findViewById(R.id.distanceFromMyFlag);

                checkLocationPermission();


                CTFCriteria ctfCriteria = new CTFCriteria();
                LocationManager locationManager = (LocationManager) this.getActivity()
                        .getSystemService(Activity.LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(ctfCriteria, true);
                Location location = locationManager.getLastKnownLocation(provider);
                db = new GameDB();

                //updateWithNewLocation(location);
                //activate the updates by the listener
                locationManager.requestLocationUpdates(provider,
                        1000,
                        0,
                        locationListener);

                if(role.equals("Stealer")){
                    mSensorManager = (SensorManager) this.getActivity()
                            .getSystemService(Activity.SENSOR_SERVICE);
                    mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                    azimuthText = view.findViewById(R.id.degreeView);
                }




                break;


            case 2:
                // code for CHAT section
                break;
        }




    }*/

    // ----------------------------------- calc ------------------------------------------------

    private double calculateAngle (double startLat, double startLong, double destLat, double destLong){
        double x = Math.cos(Math.toRadians(destLat))
                * Math.sin(Math.toRadians(destLong - startLong));

        double y = Math.cos(Math.toRadians(startLat))
                * Math.sin(Math.toRadians(destLat))
                - Math.sin(Math.toRadians(startLat))
                * Math.cos(Math.toRadians(destLat))
                * Math.cos(Math.toRadians(destLong - startLong));

        double res = Math.toDegrees(Math.atan2(x, y));

        return (res < 0) ? 360 + res : res;
    }



}