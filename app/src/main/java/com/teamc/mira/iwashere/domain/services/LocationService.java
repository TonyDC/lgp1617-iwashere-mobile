package com.teamc.mira.iwashere.domain.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Class LocationService tracks location of a device
 */
public class LocationService extends Service implements LocationListener {

    private static final String LOG_TAG = LocationService.class.getSimpleName();

    private Location aLocation = null;                                  // location represented by latitude & longitude
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;      // minimum distance for updates
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 5;      // minimum time for updates
    private LocationManager aLocationManager;                           // location manager gets access to user`s GPS & Internet connection
    //private LocationListener aLocationListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // getting location
        getCurrentLocation();
        // starting service
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        // starting service in new thread with CPU background priority
        HandlerThread thread = new HandlerThread("Location Service", 10);
        thread.start();
    }

    /**
     * Method finds user`s location by checking GPS & Internet in user`s device
     */
    private void getCurrentLocation() {
        try {
            aLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean aIsGPSEnabled = aLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);           // getting GPS statusus
            boolean aIsInternetEnabled = aLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  // getting Internet status

            if (aIsInternetEnabled) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    sendPermissionIntent();
                    return;
                }
                // location will be requested in certain time and distance
                aLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d(LOG_TAG, "Network Enabled");
                if (aLocationManager != null) {
                    aLocation = aLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if (aIsGPSEnabled) {
                if (aLocation == null) {
                    // location will be requested in certain time and distance
                    aLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(LOG_TAG, "GPS Enabled");
                    if (aLocationManager != null) {
                        aLocation = aLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        aLocationManager.removeUpdates(this);
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        aLocation = location;
        Toast.makeText(getApplicationContext(), "new location", Toast.LENGTH_LONG).show();
        // creating new intent
        Intent intent = new Intent("New Location");
        intent.putExtra("latitude", String.valueOf(aLocation.getLatitude()));
        intent.putExtra("longitude", String.valueOf(aLocation.getLongitude()));
        Log.d(LOG_TAG, "New Location: " + String.valueOf(aLocation.getLatitude()) +
                " " + String.valueOf(aLocation.getLongitude()));
        // sending intent
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            sendPermissionIntent();
            return;
        }
        aLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    @Override
    public void onProviderDisabled(String s) {
        aLocationManager.removeUpdates(this);
    }

    private void sendPermissionIntent() {
        // creating new intent
        Intent intent = new Intent("PERMISSION NEEDED");
        intent.putExtra("permission", String.valueOf(Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d(LOG_TAG, "PERMISSION NEEDED: " + String.valueOf(aLocation.getLongitude()));
        // sending intent
        sendBroadcast(intent);
    }
}