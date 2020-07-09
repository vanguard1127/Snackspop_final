package com.snackspop.snackspopnew.Location;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.snackspop.snackspopnew.Utils.AppUtils;

import java.util.ArrayList;

public class LocationUpdateService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context mContext;

    protected static final String TAG = "LocationUpdateService";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 15000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /*
      the smallest displacement in METERS the user must move between location updates.
     */
    private static final long MINIMUM_DISTANCE_COVER = 100;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    private ArrayList<LocationChange> locationChangeCallback = new ArrayList<LocationChange>();

    private LocationBinder binder = new LocationBinder();

    private Location currentLocation;

    SharedPreferences mPreferences;

    //-------------------Location Variables and methods

    public static final String LOCATION_LAT = "loc_lat";
    public static final String LOCATION_LONG = "loc_long";
    public static final String LOCATION_SERVICE_LAT = "loc_service_lat";
    public static final String LOCATION_SERVICE_LONG = "loc_service_long";
    public static String PREF_NAME = "PREF_LOCATION";
    protected SharedPreferences sharedPreference;
    BroadcastReceiver mReceiver;
    IntentFilter intentFilter;

    private void initSharedPrefrence() {
        if (sharedPreference == null)
            sharedPreference = getApplication().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    //------------------------------------------------------------

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;
    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            if( mLastLocation.getLatitude() != location.getLatitude() ||
                    mLastLocation.getLongitude() != location.getLongitude() ) {
                mLastLocation.set(location);
                saveCurrentLocation(location);
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

       /* if (sharedPreference == null)
            initSharedPrefrence();

        sharedPreference.edit().putString(LOCATION_LAT, "41.820135").apply();
        sharedPreference.edit().putString(LOCATION_LONG, "123.440950").apply();*/


        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        toggleLocationSettingReceiver(this, true);
        mContext = this;
        Log.e(TAG, "onCreate()"); intentFilter = new IntentFilter(
                AppUtils.BROADCAST.BR_CLOSE_LOCATION_SERVICE);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(AppUtils.BROADCAST.BR_CLOSE_LOCATION_SERVICE)) {
                    LocationUpdateService.this.stopSelf();
                }
            }
        };
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildGoogleApiClient();

        return START_STICKY;
    }

    public class LocationBinder extends Binder {
        public LocationUpdateService getService() {
            return LocationUpdateService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();

        mGoogleApiClient.connect();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        //Set the minimum displacement between location updates in meters
        //the smallest displacement in meters the user must move between location updates.
        mLocationRequest.setSmallestDisplacement(MINIMUM_DISTANCE_COVER);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(Bundle bundle) {
        if(mGoogleApiClient.isConnected())
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    public void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void removeLocationChangeListener(LocationChange locationListener) {
        this.locationChangeCallback.remove(locationListener);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "===Current Location===>" + location.getLatitude() + " - " + location.getLongitude());
        currentLocation = location;
        saveCurrentLocation(currentLocation);
//        App.instance().setCurrentLocation(location);

        for (LocationChange locationListener : locationChangeCallback) {
            locationListener.notifyLocationChange(location);
        }


    }

    public void saveCurrentLocation(Location location) {

        if (sharedPreference == null)
            initSharedPrefrence();

        sharedPreference.edit().putString(LOCATION_SERVICE_LAT, String.valueOf(location.getLatitude())).apply();
        sharedPreference.edit().putString(LOCATION_SERVICE_LONG, String.valueOf(location.getLongitude())).apply();

        sendBroadcast(new Intent(AppUtils.BROADCAST.BR_UPDATE_LOCATION));

//        StartAsyncUpdateLocation();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        toggleLocationSettingReceiver(this, false);
        this.unregisterReceiver(mReceiver);

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        super.onDestroy();
    }

    /*
        This will enable/disable Location Setting Broadcast receiver.
        It will enable broadcast receiver to listen for location Setting changes in device.
     */
    private void toggleLocationSettingReceiver(Context context, boolean enable) {
        int flag = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        ComponentName receiver = new ComponentName(context,
                LocationSettingChangeReceiver.class);

        context.getPackageManager().setComponentEnabledSetting(receiver, flag,
                PackageManager.DONT_KILL_APP);
    }
}
