package com.conductor.ptms.conductor;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationService extends Service {


    public LocationService() {
    }


    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    SharedPreferences shared;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        LocationManager locationManager;
        LocationListener locationListener;
        String inputLongitude, inputLatitude;
        DatabaseReference databaseLocation = FirebaseDatabase.getInstance().getReference().child("Location");
        DatabaseReference databaseCity = FirebaseDatabase.getInstance().getReference().child
                ("City");
        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
            final String bus_id = shared.getString("bus_id","Error");
//            String id = "bus_102";
            final Double latitide = location.getLatitude();
            final Double longitude = location.getLongitude();
            com.conductor.ptms.conductor.Location l = new com.conductor.ptms.conductor.Location
                    (longitude,latitide);
            databaseLocation.child(bus_id).setValue(l);

            //TODO : Reduce the Number of Iterations
            databaseCity.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        inputLatitude = d.child("Latitude").getValue(String.class);
                        inputLongitude= d.child("longitude").getValue(String.class);
                        String cityName = d.child("City_Name").getValue(String.class);
                        Log.d("CHECK",inputLatitude +" "+inputLongitude+" "+ cityName);
                        Double lati = Double.parseDouble(inputLatitude)*100.0;
                        Double longi = Double.parseDouble(inputLongitude)*100.0;
//                        Double
                        int a,b;
                        Log.d("CHECK" , "VALUES1"+inputLatitude+" "+latitide);
                        Log.d("CHECK" , "VALUES2"+inputLongitude+" "+longitude);

                        if(lati - latitide*100 > 0)
                        {
                            a = (int)(lati - latitide*100);
                        }
                        else {
                            a = (int)(latitide*100 - lati);

                        }
                        if(longi - longitude*100 > 0){
                            b = (int)(longi - longitude*100);
                        }
                        else {
                            b = (int)(longitude*100 - longi);

                        }
//                        int a = (int)(lati*100.0 - latitide*100.0);
//                        int b = (int)(longi*100.0 - longitude*100.0);
                        Log.d("CHECK FINAL",a+" "+b);
                        if(a <= 2 && b <= 2 ){
                            databaseLocation.child(bus_id).child("lastVisitedCity").setValue
                                    (cityName);
                    }
                }
            }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
//        databaseLocation = FirebaseDatabase.getInstance().getReference("location");

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
