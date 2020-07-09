package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Location.LocationUpdateService;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SetAddressActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_address_line1, et_address_line2, et_country, et_state, et_city, et_zip;
    Button bt_save;

    String userId;
    Context mContext;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;

    protected SharedPreferences LocationSharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);
        mContext = this;


        if (LocationSharedPreference == null)
            LocationSharedPreference = ((Activity) mContext).getApplication().getSharedPreferences(LocationUpdateService.PREF_NAME, MODE_PRIVATE);
        LatLng myCurrentPositions = new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LAT, "0")),
                Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LONG, "0")));

        initUi();
        new GetLocationAsync(myCurrentPositions.latitude, myCurrentPositions.longitude).execute();
    }

    private void initUi() {
        mProgressView = findViewById(R.id.progress_bar);
        et_address_line1 = (EditText) findViewById(R.id.et_address_line1);
        et_address_line2 = (EditText) findViewById(R.id.et_address_line2);
        et_country = (EditText) findViewById(R.id.et_country);
        et_state = (EditText) findViewById(R.id.et_state);
        et_city = (EditText) findViewById(R.id.et_city);
        et_zip = (EditText) findViewById(R.id.et_zip);
        bt_save = (Button) findViewById(R.id.bt_save);

        userId = AppUtils.getUserId(mContext);
        try {
            JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));


            et_address_line1.setText(profileObj.optString("address_line1"));
            et_address_line2.setText(profileObj.optString("address_line2"));
            et_country.setText(profileObj.optString("country"));
            et_state.setText(profileObj.optString("state"));
            et_city.setText(profileObj.optString("city"));
            et_zip.setText(profileObj.optString("zipcode"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bt_save.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
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

    String city, state, zipcode, country, address, address2;
    private class GetLocationAsync extends AsyncTask<String, Void, String> {

        // boolean duplicateResponse;
        double x, y;
        StringBuilder str;
        List<Address> addresses;
        String location;
        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            showProgress(true);
            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute() {
            // Address.setText(" Getting location ");
        }
        String getType(String types){
            String type = "none";
            if( types.contains("\"locality\"") )
                return "city";
            if( types.contains("\"administrative_area_level_1\"") )
                return "state";
            if( types.contains("\"country\"") )
                return "country";
            if( types.contains("\"postal_code\"") )
                return "zipcode";
            if( types.contains("\"sublocality\"") )
                return "address2";
            return type;
        }
        @Override
        protected String doInBackground(String... params) {
                HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+x+","+y+"&sensor=false");
                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                StringBuilder stringBuilder = new StringBuilder();

                try {
                    response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    InputStream stream = entity.getContent();
                    int b;
                    while ((b = stream.read()) != -1) {
                        stringBuilder.append((char) b);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(stringBuilder.toString());
                    JSONObject location  = jsonObject.getJSONArray("results").getJSONObject(0);;
                    JSONArray addressComponents = location.getJSONArray("address_components");
                    for(int i = 0; i < addressComponents.length(); i++){
                        JSONObject objComponent = addressComponents.getJSONObject(i);
                        if( getType(objComponent.optString("types")).equals("city") ){
                            city = objComponent.optString("long_name");
                        }else if( getType(objComponent.optString("types")).equals("state") ){
                            state = objComponent.optString("long_name");
                        }else if( getType(objComponent.optString("types")).equals("country") ){
                            country = objComponent.optString("long_name");
                        }else if( getType(objComponent.optString("types")).equals("zipcode") ){
                            zipcode = objComponent.optString("long_name");
                        }else if( getType(objComponent.optString("types")).equals("address2") ){
                            address2 = objComponent.optString("long_name");
                        }else {
                            if( address == null )
                                address = objComponent.optString("long_name");
                            else
                                address = address + "," + objComponent.optString("long_name");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //  Toast.makeText(MapViewActivity.this, cityName, Toast.LENGTH_SHORT).show();
                showProgress(false);
                et_address_line1.setText(address);
                et_address_line2.setText(address2);
                et_country.setText(country);
                et_state.setText(state);
                et_city.setText(city);
                et_zip.setText(zipcode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

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
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("address_line1", et_address_line1.getText().toString());
            jObj.put("address_line2", et_address_line2.getText().toString());
            jObj.put("country", et_country.getText().toString());
            jObj.put("state", et_state.getText().toString());
            jObj.put("city", et_city.getText().toString());
            jObj.put("zipcode", et_zip.getText().toString());
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                if (TextUtils.isEmpty(et_address_line1.getText())) {
                    et_address_line1.setError(getResources().getString(R.string.message_required_field));
                    et_address_line1.requestFocus();
                } else if (TextUtils.isEmpty(et_country.getText())) {
                    et_country.setError(getResources().getString(R.string.message_required_field));
                    et_country.requestFocus();
                } else if (TextUtils.isEmpty(et_state.getText())) {
                    et_state.setError(getResources().getString(R.string.message_required_field));
                    et_state.requestFocus();
                } else if (TextUtils.isEmpty(et_city.getText())) {
                    et_city.setError(getResources().getString(R.string.message_required_field));
                    et_city.requestFocus();
                } else if (TextUtils.isEmpty(et_zip.getText())) {
                    et_zip.setError(getResources().getString(R.string.message_required_field));
                    et_zip.requestFocus();
                } else {
                    showProgress(true);
                    StartAsyncUpdateAddress();
                }
                break;
            case R.id.iv_back:
                super.onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}
