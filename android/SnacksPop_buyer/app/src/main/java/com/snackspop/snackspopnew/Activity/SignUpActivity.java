package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Button bt_create_account;
    EditText et_first_name;
    EditText et_last_name;
    EditText et_phone_number;
    EditText et_password;
    EditText et_email;
    ImageView iv_back;
    Context mContext;
    String regID;
    CheckBox cb_tc;
    TextView tv_tc;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mContext = this;
        FirebaseApp.initializeApp(mContext);
        iniUi();
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            regID = FirebaseInstanceId.getInstance().getToken();
        }
    }

    private void iniUi() {

        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        mProgressView = findViewById(R.id.progress_bar);
        cb_tc = (CheckBox) findViewById(R.id.cb_tc);
        cb_tc.setChecked(true);
        findViewById(R.id.tv_tc).setOnClickListener(this);

        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_password = (EditText) findViewById(R.id.et_password);

        et_email = (EditText) findViewById(R.id.et_email);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        bt_create_account = (Button) findViewById(R.id.bt_create_account);
        bt_create_account.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_create_account:

                if (TextUtils.isEmpty(regID)) {

                    regID = FirebaseInstanceId.getInstance().getToken();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(regID))
                                signUp();
                            else {
                                Snackbar.make(bt_create_account, getResources().getString(
                                        R.string.message_server_down_or_internet), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                showProgress(false);
                            }

                        }
                    }, 3000);

                } else
                    signUp();

                break;
            case R.id.iv_back:
                super.onBackPressed();
                break;
            case R.id.tv_tc:
                startActivity(new Intent(mContext, TermsServicesActivity.class));
                break;
        }

    }

    private void signUp() {
        if (TextUtils.isEmpty(et_first_name.getText().toString())) {
            et_first_name.setError(getResources().getString(R.string.message_required_field));
            et_first_name.requestFocus();
        } else if (TextUtils.isEmpty(et_last_name.getText().toString())) {
            et_last_name.setError(getResources().getString(R.string.message_required_field));
            et_last_name.requestFocus();
        } else if (TextUtils.isEmpty(et_email.getText().toString())) {
            et_email.setError(getResources().getString(R.string.message_required_field));
            et_email.requestFocus();
        } else if (!AppUtils.isValidEmail(et_email.getText().toString())) {
            et_email.setError(getResources().getString(R.string.message_invalid_email));
            et_email.requestFocus();
        } else if (TextUtils.isEmpty(et_phone_number.getText().toString())) {
            et_phone_number.setError(getResources().getString(R.string.message_required_field));
            et_phone_number.requestFocus();
        } else if (TextUtils.isEmpty(et_password.getText().toString())) {
            et_password.setError(getResources().getString(R.string.message_required_field));
            et_password.requestFocus();
        }  else if (!cb_tc.isChecked()) {

            new AlertDialog.Builder(mContext)
                    .setTitle("Alert")
                    .setMessage("Please check terms and conditions")
                    .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(R.drawable.ic_logo_cir)
                    .setCancelable(false)
                    .show();

        } else {
            showProgress(true);
            StartAsyncSigUp();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

    private void StartAsyncSigUp() {

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.SIGNUP,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.SIGIN;
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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.SIGNUP))
                    onSigninSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.SIGNUP))
                    onSigninFailedDesc(Message);
                break;
            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.SIGNUP))
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

    protected void onSigninFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onSigninSuccess() {
        try {
            AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, mFinalJsonObj.getJSONObject("user").toString());
            AppUtils.setPreferences(AppUtils.PREFS.USER_TOKEN, mContext, mFinalJsonObj.getString("token"));
            AppUtils.setPreferences(AppUtils.PREFS.USER_ID, mContext, mFinalJsonObj.getJSONObject("user").getString("id"));
            AppUtils.setPreferences(AppUtils.PREFS.IS_LOGED_IN, mContext, true);
            startActivity(new Intent(mContext, HomeActivity.class));
            finish();


            sendBroadcast(new Intent(AppUtils.BROADCAST.BR_CLOSE_ACTIVITY_LOGIN));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("email", et_email.getText().toString());
            jObj.put("password", et_password.getText().toString());
            jObj.put("first_name", et_first_name.getText().toString());
            jObj.put("last_name", et_last_name.getText().toString());
            jObj.put("phone_number", et_phone_number.getText().toString());
            jObj.put("device_type", 0);
            jObj.put("device_id", regID);
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}
