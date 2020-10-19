package com.junipero.capturetheflag;

import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);

        Criteria crta = new Criteria();
        crta.setAccuracy(Criteria.ACCURACY_FINE);
        crta.setAltitudeRequired(false);
        crta.setBearingRequired(false);
        crta.setCostAllowed(true);
        crta.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(crta, true);


        checkLocationPermission();
        Location location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);

        locationManager.requestLocationUpdates(provider, 1000, 0,
                locationListener);


    }

    //---------------- END ON CREATE -------------------------

    private final LocationListener locationListener = new LocationListener()
    {

        @Override
        public void onLocationChanged(Location location) { updateWithNewLocation(location); }

        @Override
        public void onProviderDisabled(String provider)
        {
            updateWithNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

    };

    private void updateWithNewLocation(Location location)
    {
        String latLong;
        TextView myLocation;
        myLocation = findViewById(R.id.myLocation);

        String addressString = "no address found";

        checkLocationPermission();

        if (location != null)
        {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            double acc = location.getAccuracy();
            latLong = "Lat:" + lat + "\nLong:" + lon + "\nAcc:" + acc + "\n";

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try
            {
                List<Address> addresses = gc.getFromLocation(latitude,
                        longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0)
                {
                    Address address = addresses.get(0);
                    sb.append(address.getAddressLine(0)).append("\n");
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                }
                addressString = sb.toString();
            } catch (Exception e) {
            }
        } else
        {
            latLong = " NO Location Found ";
        }

        myLocation.setText(latLong  + addressString);
    }

    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

}