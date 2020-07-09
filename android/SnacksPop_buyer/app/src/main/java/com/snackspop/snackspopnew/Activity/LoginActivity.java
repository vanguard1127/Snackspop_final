package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;
import com.snackspop.snackspopnew.Utils.LogCat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    ImageView bt_fb;
    ImageView bt_gp;
    Button bt_login;
    TextView tv_forgot_password;
    TextView tv_create_new_account;
    EditText et_password;
    EditText et_email;
    Context mContext;
    String regID;


    BroadcastReceiver mReceiver;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    CallbackManager callbackManager;

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_login);
        initUi();
        FirebaseApp.initializeApp(mContext);
        Log.d("LoginAcivity" , "1");
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            regID = FirebaseInstanceId.getInstance().getToken();
            Log.d("LoginAcivity" , "RegID=" +regID);
        }
    }

    private void initUi() {

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        mProgressView = findViewById(R.id.progress_bar);

        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_create_new_account = (TextView) findViewById(R.id.tv_create_new_account);
        tv_forgot_password.setOnClickListener(this);
        tv_create_new_account.setOnClickListener(this);

        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);

        //fb login code-----------------------------------------------
        bt_fb = (ImageView) findViewById(R.id.bt_fb);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final String accessToken = loginResult.getAccessToken()
                        .getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                Log.i("LoginActivity",
                                        response.toString());
                                try {
                                    String name = object.getString("name");
                                    String firstName, lastName;
                                    firstName = TextUtils.isEmpty(name) ? "" : name.split(" ")[0];
                                    lastName = TextUtils.isEmpty(name) ? "" : name.split(" ").length > 1 ? name.split(" ")[1] : "";
                                    String email = TextUtils.isEmpty(object.optString("email"))?"":object.optString("email");
                                    showProgress(true);
                                    StartAsyncSocialLogin(object.getString("id"), email, firstName, lastName);
//                                    LogCat.e(name + "  " + userId + "  " + object.getString("id"));
                                    LoginManager.getInstance().logOut();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                LogCat.e("fb Login " + loginResult.getAccessToken() + "   --->   " + loginResult.toString());
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        bt_fb.setOnClickListener(this);
        //-------------------------------------------------------------------


        //-----Google plus code -----------------------
        bt_gp = (ImageView) findViewById(R.id.bt_gp);
        // Configure sign-in to request the user's ID, userId address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        bt_gp.setOnClickListener(this);

        //---------------------------------------------
        IntentFilter intentFilter = new IntentFilter(
                AppUtils.BROADCAST.BR_CLOSE_ACTIVITY_LOGIN);
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppUtils.BROADCAST.BR_CLOSE_ACTIVITY_LOGIN)) {
                    finish();

                }
            }
        };
        mContext.registerReceiver(mReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_fb:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                break;

            case R.id.bt_gp:
                signIn();
                break;

            case R.id.tv_create_new_account:
                startActivity(new Intent(mContext, SignUpActivity.class));
                break;

            case R.id.tv_forgot_password:
                startActivity(new Intent(mContext, ForgotPasswordActivity.class));
                break;

            case R.id.bt_login:

                if (TextUtils.isEmpty(regID)) {

                    regID = FirebaseInstanceId.getInstance().getToken();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(regID))
                                login();
                            else {

                                Snackbar.make(bt_login, getResources().getString(
                                        R.string.message_server_down_or_internet), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                showProgress(false);
                            }

                        }
                    }, 3000);

                } else
                    login();
                break;
        }
    }

    private void login() {
       // showProgress(true);
       // StartAsyncSocialLogin("test_social", "social@gmail.com", "", "");

        if (TextUtils.isEmpty(et_email.getText().toString())) {
            et_email.setError(getResources().getString(R.string.message_required_field));
            et_email.requestFocus();
        } else if (!AppUtils.isValidEmail(et_email.getText().toString())) {
            et_email.setError(getResources().getString(R.string.message_invalid_email));
            et_email.requestFocus();
        } else if (TextUtils.isEmpty(et_password.getText().toString())) {
            //et_password.setError(getResources().getString(R.string.message_required_field));
            //et_password.requestFocus();
        } else {
            showProgress(true);
            StartAsyncLogin();
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        LogCat.e("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String firstName, lastName;
            firstName = acct.getDisplayName()!=null && TextUtils.isEmpty(acct.getDisplayName()) ? "" : acct.getDisplayName().split(" ")[0];
            lastName = acct.getDisplayName()!=null && TextUtils.isEmpty(acct.getDisplayName()) ? "" : acct.getDisplayName().split(" ").length > 1 ? acct.getDisplayName().split(" ")[1] : "";
            showProgress(true);
            StartAsyncSocialLogin(acct.getId(), acct.getEmail(), firstName, lastName);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        } else {
            // Signed out, show unauthenticated UI.
            LogCat.e(result.getStatus().getStatusMessage() + result.getStatus().getStatus());
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LogCat.e("onConnectionFailed:" + connectionResult);
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

    private void StartAsyncSocialLogin(String token, String email, String firstName, String lastName) {
        AppUtils.setPreferences(AppUtils.PREFS.IS_SOCIAL, mContext, "1");
        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair(token, email, firstName, lastName);
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.LOGIN,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.SOCIAL_SIGNIN;
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
                /*Snackbar.make(mProgressView, getResources().getString(
                        R.string.message_server_down), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        } else {
            showProgress(false);
            /*Snackbar.make(mProgressView, getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
        }
    }

    private void StartAsyncLogin() {
        AppUtils.setPreferences(AppUtils.PREFS.IS_SOCIAL, mContext, "0");
        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.LOGIN,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.LOGIN;
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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.LOGIN))
                    onLoginSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.LOGIN))
                    onLoginFailedDesc(Message);
                break;
            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.LOGIN))
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

    protected void onLoginFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onLoginSuccess() {
        try {
            AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, mFinalJsonObj.getJSONObject("user").toString());
            AppUtils.setPreferences(AppUtils.PREFS.USER_TOKEN, mContext, mFinalJsonObj.getString("token"));
            AppUtils.setPreferences(AppUtils.PREFS.USER_ID, mContext, mFinalJsonObj.getJSONObject("user").getString("id"));
            AppUtils.setPreferences(AppUtils.PREFS.IS_LOGED_IN, mContext, true);

            if (AppUtils.chatting_flag)
            {

                startActivity(new Intent(mContext, ChattingActivity.class).putExtra(AppUtils.EXTRA.chatting_user_id, AppUtils.chatting_userid)
                        .putExtra(AppUtils.EXTRA.chatting_user_name ,AppUtils.chatting_username).putExtra(AppUtils.EXTRA.chatting_user_image , AppUtils.chatting_userimageurl).putExtra(AppUtils.EXTRA.chatting_item_id , AppUtils.chatting_itemid));
            }
            else {
                startActivity(new Intent(mContext, HomeActivity.class));
            }
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
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    private String getNameValuePair(String token, String email, String firstName, String lastName) {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("email", email);
            jObj.put("social_token", token);
            jObj.put("first_name", firstName);
            jObj.put("last_name", lastName);
            jObj.put("device_type", 0);
            if (regID == null)
                regID = "";
            jObj.put("device_id", regID);
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}
