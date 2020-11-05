package com.junipero.capturetheflag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;



public class LocationUpdater {
    private LocationManager locationManager;
    private String provider;
    private double latitude;
    private double longitude;
    private double accuracy;
    private double degree;
    private String actualPosition = "ciao";
    private TextView myLocation;

    @SuppressLint("MissingPermission")
    public LocationUpdater (Context mContext, TextView myLocation) {
        String context = Context.LOCATION_SERVICE;
        // associated a textview field just for debug
        this.myLocation = myLocation;
        // creating Criteria to manage location data required for the game
        CTFCriteria ctfCriteria = new CTFCriteria();
        locationManager = (LocationManager)mContext.getSystemService(context);
        provider = locationManager.getBestProvider(ctfCriteria, true);
        assert provider != null;
        Location location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        //activate the updates by the listener
        locationManager.requestLocationUpdates(provider,
                1000,
                0,
                locationListener);
    }



    // ----------------- location data getter ----------------------
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    // non implemented yet =)
    public double getDegree() {
        return 0.0;
    }

    public String getActualPosition() {
        return actualPosition;
    }

    public LocationManager getLocationManager() { return locationManager; }

    public LocationListener getLocationListener() { return locationListener; }

    public String getProvider() { return provider; }

    // ------------------- LocationUpdater Core ------------------------

    @SuppressLint("MissingPermission")
    public void getRealTimeUpdates() {
        locationManager.requestLocationUpdates(provider,
                1000,
                0,
                locationListener);
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            myLocation.setText(actualPosition);
        }

        @Override
        public void onProviderDisabled(String provider) { updateWithNewLocation(null); }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

    };

    private void updateWithNewLocation (Location location) {

        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
            // degree missing ¯\_(ツ)_/¯

            // EXTRA
            Date currentTime = Calendar.getInstance().getTime();
            // updating the string for the data retrieved
            actualPosition =    "Latitude:" + latitude +
                    "\nLongitude:" + longitude +
                    "\nAccuracy:" + accuracy +
                    "\nDEGREE:" + degree + "\n" +
                    // the time is just for debug =)
                    "\n TIME: " + currentTime;
        }
    }



}
