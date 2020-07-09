package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.snackspop.snackspopnew.Activity.Fragment.MySupportMapFragment;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Location.LocationUpdateService;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;
import com.snackspop.snackspopnew.Utils.LogCat;
import org.json.JSONObject;

public class SetLocationActivity extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener {


    private GoogleMap googleMap;
    SupportMapFragment mapFragment;
    LatLng myCurrentPositions, centerOfMap;
    Context mContext;
    TextView et_address;
    Button bt_save;
    protected SharedPreferences LocationSharedPreference;
    private static final int ACTIVITY_UPDATE_ADDRESS = 11;
    String userId;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;
    ImageView imgLocation;
    BroadcastReceiver mLocationReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        mContext = this;

        LocationSharedPreference = ((Activity) mContext).getApplication().getSharedPreferences(LocationUpdateService.PREF_NAME, MODE_PRIVATE);

        myCurrentPositions = new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_LAT, "0")),
                Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_LONG, "0")));
        if (myCurrentPositions.latitude == 0) {
            Location location = getLocation();
            if( location != null ){
                myCurrentPositions = new LatLng(location.getLatitude(),  location.getLongitude());
                LocationSharedPreference.edit().putString(LocationUpdateService.LOCATION_LAT, String.valueOf(location.getLatitude())).apply();
                LocationSharedPreference.edit().putString(LocationUpdateService.LOCATION_LONG, String.valueOf(location.getLongitude())).apply();
            }
        }
        try {
            if (PreferenceManager.getDefaultSharedPreferences(mContext).
                    getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
                JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));


//                if (TextUtils.isEmpty(profileObj.optString("address_line1")) &&
//                        myCurrentPositions.latitude != 0 ) {
//                    startActivityForResult(new Intent(mContext, SetAddressActivity.class), ACTIVITY_UPDATE_ADDRESS);
//                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        userId = AppUtils.getUserId(mContext);
        initUi();

    }

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public Location getLocation() {
        Location location = null;
        try {

            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);


                for (String provider : locationManager.getProviders(criteria, true)) {
                    if (provider.contains("gps"))
                        return null;
                }
                //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return null;
            } else {

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled && location == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return null;
                        }
                    }


                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                            //  Toast.makeText(this, "Location is Changed", Toast.LENGTH_SHORT).show();

                        }

                    }
                }

                // get location from Network Provider
                if (isNetworkEnabled && location == null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                            //  Toast.makeText(this, "Location is Changed", Toast.LENGTH_SHORT).show();

                        }

                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void initUi() {
        mProgressView = findViewById(R.id.progress_bar);
        et_address = (TextView) findViewById(R.id.et_address);
        bt_save = (Button) findViewById(R.id.bt_save);
        et_address.setVisibility(View.GONE);
        imgLocation = (ImageView)findViewById(R.id.imgLocation);
        imgLocation.setVisibility(View.GONE);

        mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_my_location).setOnClickListener(this);
        bt_save.setOnClickListener(this);

        if (PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
            et_address.setOnClickListener(this);
            et_address.setVisibility(View.VISIBLE);
            try {
                JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));
                String address = AppUtils.getJSONStringValue(profileObj, "address_line1") +  ","
                        + AppUtils.getJSONStringValue(profileObj, "address_line2") +  ","
                        + AppUtils.getJSONStringValue(profileObj, "city") +  ","
                        + AppUtils.getJSONStringValue(profileObj, "state") +  ","
                        + AppUtils.getJSONStringValue(profileObj, "country") +  ","
                        + AppUtils.getJSONStringValue(profileObj, "zipcode");
                if( address.length() > 5 )
                    et_address.setText(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                super.onBackPressed();
                break;

            case R.id.bt_save:
                if( centerOfMap == null ){
                    Toast.makeText(this, "There is no location. Please set location", Toast.LENGTH_SHORT).show();
                    return;
                }

                LocationSharedPreference.edit().putString(LocationUpdateService.LOCATION_LAT, String.valueOf(centerOfMap.latitude)).apply();
                LocationSharedPreference.edit().putString(LocationUpdateService.LOCATION_LONG, String.valueOf(centerOfMap.longitude)).apply();

                if (PreferenceManager.getDefaultSharedPreferences(this).
                        getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
                    StartAsyncUpdateAddress();
                }
                else
                {
                    setResult(Activity.RESULT_OK, new Intent().putExtra(AppUtils.EXTRA.LOC_LAT, centerOfMap.latitude)
                            .putExtra(AppUtils.EXTRA.LOC_LNG, centerOfMap.longitude));
                    finish();
                }
                break;
            case R.id.iv_my_location:
                if (googleMap != null) {
                    myCurrentPositions = new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LAT, "0")),
                            Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LONG, "0")));
                    if (myCurrentPositions.latitude != 0) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(myCurrentPositions).zoom(14f).build();

                        googleMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                        imgLocation.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.et_address:
                if( myCurrentPositions.latitude != 0 ) {
                    startActivityForResult(new Intent(mContext, SetAddressActivity.class), ACTIVITY_UPDATE_ADDRESS);
                }else
                {
                    Toast.makeText(this, "Please wait to get user location", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_UPDATE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                try {
                    JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));
                    String address = "";
                    address = address + ((TextUtils.isEmpty(profileObj.optString("address_line1")) || profileObj.optString("zipcode").equals("null")) ? "" : (profileObj.optString("address_line1") + ", "))
                            + ((TextUtils.isEmpty(profileObj.optString("address_line2")) || profileObj.optString("zipcode").equals("null")) ? "" : (profileObj.optString("address_line2") + ", "))
                            + ((TextUtils.isEmpty(profileObj.optString("country")) || profileObj.optString("zipcode").equals("null")) ? "" : (profileObj.optString("country") + ", "))
                            + ((TextUtils.isEmpty(profileObj.optString("state")) || profileObj.optString("zipcode").equals("null")) ? "" : (profileObj.optString("state") + ", "))
                            + ((TextUtils.isEmpty(profileObj.optString("city")) || profileObj.optString("zipcode").equals("null") ) ? "" : (profileObj.optString("city") + ", "))
                            + ((TextUtils.isEmpty(profileObj.optString("zipcode")) || profileObj.optString("zipcode").equals("null")) ? "" : (profileObj.optString("zipcode")));

                    et_address.setText(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                startActivity(new Intent(mContext, HomeActivity.class));
                finish();

            }
        }
    }


    @Override
    public void onMapReady(GoogleMap mGoogleMap) {
        googleMap = mGoogleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }else{
                this.googleMap.setMyLocationEnabled(true);
            }
        }else
            this.googleMap.setMyLocationEnabled(true);


        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                centerOfMap = googleMap.getCameraPosition().target;

            }
        });
        ((MySupportMapFragment) mapFragment)
                .setCustomEventListener(new MySupportMapFragment.OnCustomEventListener() {
                    @Override
                    public void onEvent(int EventType) {
                        LogCat.e(EventType + "" + MotionEvent.ACTION_UP + "");

                    }
                });
        if (googleMap != null) {
            if (myCurrentPositions.latitude == 0)
                myCurrentPositions = new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_LAT, "0")),
                        Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_LONG, "0")));
            if (myCurrentPositions.latitude != 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(myCurrentPositions).zoom(14f).build();
                 googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                imgLocation.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent(AppUtils.BROADCAST.BR_CLOSE_LOCATION_SERVICE));
        super.onDestroy();

    }


    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void StartAsyncUpdateAddress() {

        showProgress(true);
        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.UPDATE,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        isPut = true;
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPDATE;
                        accessToken = AppUtils.getAccessToken(mContext);
                        return super.doInBackground(params);
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        super.onPostExecute(result);

                        mFinalJsonObj = finalResult;

                        // finalResult.get("")
                        onCustomPostExecuteForTokens(result, MESSAGE, MethodName);
                    }
                };
                mAsyncForAll.execute();
            } else {
                Snackbar.make(mProgressView, getResources().getString(
                        R.string.message_server_down), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else {
            showProgress(false);
            Snackbar.make(mProgressView, getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void onCustomPostExecuteForTokens(int result, String Message, String ClassName) {
        showProgress(false);
        switch (result) {
            case AppUtils.Async_SUCCESS:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE))
                    onUpdateAddressSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE))
                    onUpdateAddressFailedDesc(Message);
                break;
            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE))
                    onServerUnreachable();
                break;
            default:
                break;
        }

    }

    protected void onServerUnreachable() {

        Snackbar.make(mProgressView, getResources().getString(
                R.string.message_server_down), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onUpdateAddressFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onUpdateAddressSuccess() {
        try {
            AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, mFinalJsonObj.getJSONObject("user").toString());
            Snackbar.make(mProgressView, getResources().getString(
                    R.string.message_Address_changed), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            setResult(Activity.RESULT_OK, new Intent().putExtra(AppUtils.EXTRA.LOC_LAT, centerOfMap.latitude)
                    .putExtra(AppUtils.EXTRA.LOC_LNG, centerOfMap.longitude));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("geo_lat", centerOfMap.latitude);
            jObj.put("geo_lng", centerOfMap.longitude);
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

}
