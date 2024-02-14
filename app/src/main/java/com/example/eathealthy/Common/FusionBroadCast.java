package com.example.eathealthy.Common;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

//By KK -- 27/10/2017 -- //

public class FusionBroadCast extends Service {
    public static final String ACTION_STOP = "stop";
    private static final long INTERVEL_TIME = 8000;
    private static final long FAST_INTERVAL = 5000;
    SessionManager sessionManager;
    private LocationRequest locationRequest;
    public static String BROADCAST_ACTION = "DATA";
    private static final float LOCATION_TRACKING_MIN_DISTANCE = 15f;
    private static final float ACCEPTED_ACCURACY = 50f;

    private final IBinder mBinder = new LocalBinder();

    // fused location api
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Helper.l( "onCreate");

        sessionManager = new SessionManager(getApplicationContext());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        createLocationRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helper.l( "onStartCommand");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_NOT_STICKY;
        }

        getLastKnownLocation();
        startPeriodicLocationUpdate();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPeriodicLocationUpdate();
        Helper.l( "onDestroy - kk stop");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location lastLocation = task.getResult();
                        storeLocation(lastLocation);
                    }
                });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(INTERVEL_TIME)
                .setFastestInterval(FAST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    storeLocation(location);
                }

                Helper.l( "locationResult start");
                for (Location location1 : locationResult.getLocations()) {
                    UpdateFireStore(location);
                    //CalculateDistanceAndStore(location);
                    Helper.l( "locationResult location " + location1.getLatitude() + " " + location1.getLongitude() + " " + location1.getAccuracy() + " " + location1.getProvider());
                }
                Helper.l( "locationResult stop");
            }
        };
    }



    private void startPeriodicLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Helper.l( "periodic update");
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void stopPeriodicLocationUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(task -> Helper.l( "removed location updates!"));
    }


    private void storeLocation(Location location) {
        Helper.l( "periodic location " + location.getLatitude() + " " + location.getLongitude() + " " + location.getAccuracy() + " " + location.getProvider());
        if (location.getAccuracy() <= ACCEPTED_ACCURACY) {
            Helper.l( "storeLocation: " + "saved!");
            sessionManager.storeVal("latest_lat", String.valueOf(location.getLatitude()));
            sessionManager.storeVal("latest_long", String.valueOf(location.getLongitude()));
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Helper.l( "onTaskRemoved: ");
        stopSelf();
        //JobDispatcher.stopSendLocationJob(getApplicationContext()); // TODO: 04/07/17 not the best place for it
    }

    public static void start(Context context) {
        context.startService(new Intent(context, FusionBroadCast.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, FusionBroadCast.class));
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public FusionBroadCast getService() {
            // Return this instance of FusionBroadCast so clients can call public methods
            return FusionBroadCast.this;
        }
    }


    private void UpdateFireStore(Location location) {

        try {
            if (location != null) {
                Helper.l("location broadcast : "+ location.getLatitude() + " : "+ location.getLongitude());
                Intent i = new Intent();
                i.setAction(FusionBroadCast.BROADCAST_ACTION);
                i.putExtra("Lat", "" + location.getLatitude());
                i.putExtra("Lon", "" + location.getLongitude());
                i.putExtra("Accuracy", "" + location.getAccuracy());
                sendBroadcast(i);

            }
        } catch (Exception e) {
            Helper.l( e.toString());
        }

    }


}