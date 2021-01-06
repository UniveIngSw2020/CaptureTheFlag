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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.junipero.capturetheflag.CTFCriteria;
import com.junipero.capturetheflag.GameDB;
import com.junipero.capturetheflag.R;

import java.util.Objects;

public class TabGameActivity extends Fragment implements SensorEventListener {

    // utilities needed to manage sensors activity
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;
    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    // declaration of views and parameters
    private TextView teamRole;
    private GameDB db;
    private double azimuthDeg;
    private Intent i;
    private String gameCode;
    private String role;
    private String team;
    private String otherTeam;
    private TextView distanceFromOtherView;
    private TextView distanceFromMyTeamFlagView;
    private ImageView compassLeft, compassRight;
    private ConstraintLayout layout;
    private int numberOfPlayers = 10;
    // db references
    private DatabaseReference lobby;
    private DatabaseReference myTeamFlagRef;
    private DatabaseReference otherTeamFlagRef;

    private double angleFromOtherFlag;
    private double angleFromMyFlag;

    private String localState = "Running";

    public TabGameActivity(){
        // empty constructor needed for fragment
    }

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
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // code for GAME section
        // get all data from previous activity
        i = this.getActivity().getIntent();
        gameCode = i.getStringExtra("gameCode");
        role = i.getStringExtra("role");
        team = i.getStringExtra("team");
        // initialization of other reusable vars
        otherTeam = (team.equals("Blue") ? "Red" : "Blue" );
        lobby = new GameDB().getDbRef().child(gameCode);
        // initialization of db references of flags in game
        myTeamFlagRef = lobby.child(team).child("Keeper");
        otherTeamFlagRef = lobby.child(otherTeam).child("Keeper");

        // initializing some views
        teamRole = view.findViewById(R.id.team_role_view);
        distanceFromOtherView = view.findViewById(R.id.distanceFromOther);
        distanceFromMyTeamFlagView = view.findViewById(R.id.distanceFromMyFlag);
        compassLeft = view.findViewById(R.id.compassLeft);  // blue as default
        compassRight = view.findViewById(R.id.compassRight);    // red as default
        layout = view.findViewById(R.id.gameLayout);

        // initialize location manager service
        checkLocationPermission();
        CTFCriteria ctfCriteria = new CTFCriteria();
        LocationManager locationManager = (LocationManager) this.getActivity()
                .getSystemService(Activity.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(ctfCriteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        //activate the updates by the LocationListener
        locationManager.requestLocationUpdates(provider,
                1000,
                0,
                locationListener);

        db = new GameDB();

        // if my role is "Stealer" I can see the flags' position
        if(role.equals("Stealer")){
            View flag_bg = view.findViewById(R.id.flagbggame);
            flag_bg.setVisibility(View.INVISIBLE);
                /* default locations, will update after the game is in running */
                otherTeamFlagRef.child("Location").child("Latitude")
                        .setValue(45.485158);
                otherTeamFlagRef.child("Location").child("Longitude")
                        .setValue(12.232011);

                myTeamFlagRef.child("Location").child("Latitude")
                        .setValue(45.485426);
                myTeamFlagRef.child("Location").child("Longitude")
                        .setValue(12.242331);

            mSensorManager = (SensorManager) this.getActivity()
                    .getSystemService(Activity.SENSOR_SERVICE);
            mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            // update compass colors if my team is Blue
            // default compasses are left:blue and right:red
            // left used always for other team's flag
            if (team.equals("Blue")){
                compassRight.setImageResource(R.drawable.arrow_blue);
                compassLeft.setImageResource(R.drawable.arrow_red);
            }
            // updating views for distances
            distanceFromOtherView.setText("Distance from\nother team's flag");
            distanceFromMyTeamFlagView.setText("Distance from\nmy team's flag");
        } else {
            compassLeft.setVisibility(View.INVISIBLE);
            compassRight.setVisibility(View.INVISIBLE);
        }

        // show the role in your team
        teamRole.setText(Html.fromHtml("You are a <b>" + team + " " + role + "</b>"));
    }

    // ------------------------------- calculate angle -----------------------------------------
    /*
     * very thanks to IGISMap:
     * https://www.igismap.com/formula-to-find-bearing-or-heading-angle-between-two-points-latitude-longitude/
    **/
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
                // Stealer options
                if (role.equals("Stealer")) {
                    // initialize array that wil contains position of flags
                    final double[] otherFlagPos = new double[2];
                    final double[] myFlagPos = new double[2];

                     lobby.addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // the snapshot will now contains actual lobby table
                            // checking if locations != null
                            if (snapshot.child(otherTeam + "/Keeper/Location/Latitude").getValue() != null
                                    && snapshot.child(otherTeam + "/Keeper/Location/Longitude").getValue() != null
                                    && snapshot.child(team + "/Keeper/Location/Latitude").getValue() != null
                                    && snapshot.child(team + "/Keeper/Location/Longitude").getValue() != null) {

                                // get Location information of other team's flag
                                otherFlagPos[0] = Double.parseDouble(String.valueOf(snapshot
                                        .child(otherTeam + "/Keeper/Location/Latitude").getValue()));
                                otherFlagPos[1] = Double.parseDouble(String.valueOf(snapshot
                                        .child(otherTeam + "/Keeper/Location/Longitude").getValue()));

                                // get location information of my team's flag
                                myFlagPos[0] = Double.parseDouble(String.valueOf(snapshot
                                        .child(team + "/Keeper/Location/Latitude").getValue()));
                                myFlagPos[1] = Double.parseDouble(String.valueOf(snapshot
                                        .child(team + "/Keeper/Location/Longitude").getValue()));

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
                                distanceFromOtherView.setText("Distance from\nother team's flag:\n" + distanceFromOtherFlag + " meters");
                                long distanceFromMyFlag = calculateDistance(location.getLatitude(),
                                        location.getLongitude(), myFlagPos[0], myFlagPos[1]);
                                distanceFromMyTeamFlagView.setText("Distance from\nmy team's flag:\n" + distanceFromMyFlag + " meters");

                                numberOfPlayers = Integer.parseInt(snapshot.child("Number of players").getValue().toString());

                                // check if actual distance from opposite team's flag is near to me
                                if (distanceFromOtherFlag < 5) {
                                    String status = snapshot.child("State").getValue().toString();
                                    // TIE when each team is winning
                                    if(status.equals(otherTeam + " is winning")){
                                        lobby.child("State").setValue("End");
                                        lobby.child("Score").setValue("Tie");
                                        //endGame("Tie");
                                    }
                                    // If my team is Winning so My team will WIN the game
                                    else if (status.equals(team + " is winning")){
                                        lobby.child("State").setValue("End");
                                        lobby.child("Score").setValue(team + " wins");
                                        //endGame(team + " wins");
                                    } else {
                                        // set state to my team os winning and re-check after
                                        lobby.child("State").setValue(team + " is winning");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                     // Keeper options to update the location in db every second
                } else if (role.equals("Keeper") && !localState.equals("End") &&
                        !localState.equals("Cancelled")) {
                    // just update position if my role is "Keeper"
                    myTeamFlagRef.child("Location").child("Latitude")
                            .setValue(location.getLatitude());
                    myTeamFlagRef.child("Location").child("Longitude")
                            .setValue(location.getLongitude());
                    // and continue running :)

                    teamRole.setText(Html.fromHtml("You are a <b>" + team + " " + role + "</b>"));

                    // State of game listener
                    lobby.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child("State").getValue() != null){
                                // End the game when the state is set to END
                                if(Objects.requireNonNull(snapshot.child("State").getValue())
                                        .toString().equals("End")){
                                    // fix to manage the onPause() method as the Keeper
                                    localState = "End";
                                }else if(Objects.requireNonNull(snapshot.child("State").getValue())
                                        .toString().equals("Cancelled")){
                                    localState = "Cancelled";
                                }
                                numberOfPlayers = Integer.parseInt(Objects
                                        .requireNonNull(snapshot.child("Number of players")
                                                .getValue()).toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
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
        // Check to ensure sensors are available before registering listeners.
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
    public void onPause() {
        super.onPause();

        // if a player leave the game, he decrease the number of players in the game
        lobby.child("Number of players").setValue(numberOfPlayers-1);

        // if the player is a "Keeper", if he left the game, the game is cancelled
        if(role.equals("Keeper") && !localState.equals("End")){
            lobby.child("State").setValue("Cancelled");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // The sensor type (as defined in the Sensor class).
        int sensorType = sensorEvent.sensor.getType();

        // The sensorEvent object is reused across calls to onSensorChanged().
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
        // then calculate the degree from flags
        azimuth = (azimuth < 0) ? (float) (2 * Math.PI + azimuth) : azimuth;
        azimuth = Math.toDegrees(azimuth);
        azimuthDeg = azimuth;

        // set the textView update in real time for every movement of the device
        // Math.floor needed to round numbers into 2 decimals number after dot
        double formula = Math.floor(((angleFromOtherFlag - azimuthDeg + 360) % 360) * 100) /100;
        double formula2 = Math.floor(((angleFromMyFlag - azimuthDeg + 360) % 360) * 100) /100;

        // update rotation of compasses in real time
        compassLeft.setRotation(Double.valueOf(formula).floatValue());
        compassRight.setRotation(Double.valueOf(formula2).floatValue());
    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    // --------------------------- PERMISSION CHECKER ---------------------------------------------

    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this.getActivity()
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this.getActivity()
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    //------------------------------------------------------------------------------------------

}
