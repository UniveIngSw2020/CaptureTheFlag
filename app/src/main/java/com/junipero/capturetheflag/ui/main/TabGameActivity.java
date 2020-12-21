package com.junipero.capturetheflag.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.junipero.capturetheflag.CTFCriteria;
import com.junipero.capturetheflag.CreateGameActivity;
import com.junipero.capturetheflag.GameDB;
import com.junipero.capturetheflag.R;
import com.junipero.capturetheflag.ScoreActivity;
import com.junipero.capturetheflag.TimerActivity;

public class TabGameActivity extends Fragment implements SensorEventListener {

    private SensorManager mSensorManager;
    Sensor mSensorAccelerometer;
    Sensor mSensorMagnetometer;
    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    TextView azimuthText;
    GameDB db;
    double azimuthDeg;
    Intent i;
    String gameCode;
    String role;
    String team;
    String otherTeam;

    DatabaseReference lobby;
    DatabaseReference myTeamFlagRef;
    DatabaseReference otherTeamFlagRef;

    TextView degreeFromOtherView;
    TextView degreeFromMyTeamFlagView;
    TextView distanceFromOtherView;
    TextView distanceFromMyTeamFlagView;

    double angleFromOtherFlag;
    double angleFromMyFlag;

    // just create the view, don't use it to initialize or execute your code
    // use onViewCreated instead :)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = null;
        root = inflater.inflate(R.layout.fragment_main, container, false);

        return root;
    }

    // put your code in onViewCreated, it is called after onCreateView
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // code for GAME section
        // get all datas from previous activity
        i = this.getActivity().getIntent();
        gameCode = i.getStringExtra("gameCode");
        role = i.getStringExtra("role");
        team = i.getStringExtra("team");
        // initialization of other resusable vars
        otherTeam = (team.equals("Blue") ? "Red" : "Blue" );
        lobby = new GameDB().getDbRef().child(gameCode);
        // initialization of db references of flags in game
        myTeamFlagRef = lobby.child(team).child("Keeper");
        otherTeamFlagRef = lobby.child(otherTeam).child("Keeper");

        // initializing some views
        azimuthText = view.findViewById(R.id.degreeView);
        degreeFromOtherView = view.findViewById(R.id.degreeFromOther);
        degreeFromMyTeamFlagView = view.findViewById(R.id.degreeFromMyTeam);
        distanceFromOtherView = view.findViewById(R.id.distanceFromOther);
        distanceFromMyTeamFlagView = view.findViewById(R.id.distanceFromMyFlag);

        checkLocationPermission();


        CTFCriteria ctfCriteria = new CTFCriteria();
        LocationManager locationManager = (LocationManager) this.getActivity()
                .getSystemService(Activity.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(ctfCriteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        db = new GameDB();

        //activate the updates by the LocationListener
        locationManager.requestLocationUpdates(provider,
                1000,
                0,
                locationListener);

        if(role.equals("Stealer")){
            mSensorManager = (SensorManager) this.getActivity()
                    .getSystemService(Activity.SENSOR_SERVICE);
            mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            degreeFromOtherView.setText("degree from other");
            degreeFromMyTeamFlagView.setText("degree from my");
            distanceFromOtherView.setText("distance from other");
            distanceFromMyTeamFlagView.setText("distance from other");
        }
    }

    // ------------------------------- calculate angle -----------------------------------------
    /*
     * very thanks to IGISMap:
     *https://www.igismap.com/formula-to-find-bearing-or-heading-angle-between-two-points-latitude-longitude/
    */
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

    // Haversine formula
    private long calculateDistance (double startLat, double startLong,
                                    double destLat, double destLong) {

        double R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(destLat - startLat);
        double dLon = Math.toRadians(destLong - startLong);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(startLat))
                * Math.cos(Math.toRadians(destLat))
                * Math.sin(dLon/2)
                * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double res = R * c; // Distance in km

        // return distance in meters
        return Math.round(res*1000);
    }

    // -------------------------- location ------------------------------------------------

    private final LocationListener locationListener = new LocationListener()
    {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationChanged(final Location location) {
            if(location != null) {

                if (role.equals("Stealer")) {
                    final double[] otherFlagPos = new double[2];
                    final double[] myFlagPos = new double[2];

                     lobby.addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // the snapshot will now contains actual lobby table
                            // get Location information of other team's flag
                            otherFlagPos[0] = Double.parseDouble(String.valueOf(snapshot
                                    .child(otherTeam + "Keeper/Location/Latitude").getValue()));
                            otherFlagPos[1] = Double.parseDouble(String.valueOf(snapshot
                                    .child(otherTeam + "Keeper/Location/Longitude").getValue()));

                            // get location information of my team's flag
                            myFlagPos[0] = Double.parseDouble(String.valueOf(snapshot
                                    .child(team + "Keeper/Location/Latitude").getValue()));
                            myFlagPos[1] = Double.parseDouble(String.valueOf(snapshot
                                    .child(team + "Keeper/Location/Longitude").getValue()));

                            // obtain degrees where opposite team's flag is placed
                            angleFromOtherFlag = calculateAngle(location.getLatitude(),
                                    location.getLongitude(),
                                    otherFlagPos[0],
                                    otherFlagPos[1]);
                            // obtain degrees where my team's flag is placed
                            angleFromMyFlag = calculateAngle(location.getLatitude(),
                                    location.getLongitude(),
                                    myFlagPos[0],
                                    myFlagPos[1]);

                            // obtain distances between my position and the two flags
                            long distanceFromOtherFlag = calculateDistance(location.getLatitude(),
                                    location.getLongitude(), otherFlagPos[0], otherFlagPos[1]);
                            distanceFromOtherView.setText(distanceFromOtherFlag + "");
                            long distanceFromMyFlag = calculateDistance(location.getLatitude(),
                                    location.getLongitude(), myFlagPos[0], myFlagPos[1]);
                            distanceFromMyTeamFlagView.setText(distanceFromMyFlag + "");

                            // check if the game is ended
                            if(snapshot.child("State").getValue().toString().equals("End")){
                                endGame(snapshot.child("Score").getValue().toString());
                            }
                            // check if actual distance from opposite team's flag is near to me
                            if (distanceFromOtherFlag < 5) {
                                String status = snapshot.child("State").getValue().toString();
                                // TIE when each team is winning
                                if(status.equals(otherTeam + " is winning")){
                                    lobby.child("State").setValue("End");
                                    lobby.child("Score").setValue("Tie");
                                    endGame("Tie");
                                }
                                // If my team is Winning so My team will WIN the game
                                else if (status.equals(team + " is winning")){
                                    lobby.child("State").setValue("End");
                                    lobby.child("Score").setValue(team + " wins");
                                    endGame(team + " wins");
                                } else {
                                    // set state to my team os winning and re-check after
                                    lobby.child("State").setValue(team + " is winning");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (role.equals("Keeper")) {
                    // just update position if my role is "Keeper"
                    myTeamFlagRef.child("Location").child("Latitude")
                            .setValue(location.getLatitude());
                    myTeamFlagRef.child("Location").child("Longitude")
                            .setValue(location.getLongitude());
                    // and continue running :)

                    db.getDbRef().child("State").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // End the game when the state is set to END
                            if(snapshot.child("State").getValue().toString().equals("End")){
                                endGame(snapshot.child("Score").getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        }

        // ---------------------------------------------------------------------------------------

        @Override
        public void onProviderDisabled(String provider) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

    };

    @Override
    public void onStart() {
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
    public void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    @SuppressLint("SetTextI18n")
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
        float[] orientationValues = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix,
                    orientationValues);
        }

        // Pull out the individual values from the array.
        double azimuth = orientationValues[0];


        //azimuth = (azimuth + 0 + 360) % 360;
        azimuth = (azimuth < 0) ? (float) (2 * Math.PI + azimuth) : azimuth;
        azimuth = Math.toDegrees(azimuth);
        azimuthDeg = azimuth;

        // set the textView update in real time for every movement of the device
        double formula = Math.floor(((angleFromOtherFlag - azimuthDeg + 360) % 360) * 100) /100;
        double formula2 = Math.floor(((angleFromMyFlag - azimuthDeg + 360) % 360) * 100) /100;
        degreeFromOtherView.setText(formula + "");
        degreeFromMyTeamFlagView.setText(formula2 + "");

    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    // --------------------------- PERMISSION CHECKER ---------------------------------------------
    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this.getActivity()
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this.getActivity()
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    //---------------------------------------- END --------------------------------------------------

    private void endGame(String score){
        Intent i = new Intent(this.getActivity(), ScoreActivity.class);
        i.putExtra("score", score);
        i.putExtra("team", team);
        startActivity(i);
    }

    //------------------------------------------------------------------------------------------
}
