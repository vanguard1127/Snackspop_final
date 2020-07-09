package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.snackspop.snackspopnew.Activity.Fragment.AboutUsFragment;
import com.snackspop.snackspopnew.Activity.Fragment.ChatFragment;
import com.snackspop.snackspopnew.Activity.Fragment.HomeFragment;
import com.snackspop.snackspopnew.Activity.Fragment.MyProductFragment;
import com.snackspop.snackspopnew.Activity.Fragment.ProfileFragment;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener , HomeFragment.OnFragmentInteractionListener ,
AboutUsFragment.OnFragmentInteractionListener , ProfileFragment.OnFragmentInteractionListener , MyProductFragment.OnFragmentInteractionListener , ChatFragment.OnFragmentInteractionListener {

    Fragment fragment = null;
    Context mContext;
    BroadcastReceiver mReceiver;
    IntentFilter intentFilter;
    boolean isDown = false;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final int permissionRequestId = 1;
    public static final int changeDeliveryChargeResult = 11;
    TextView tv_user_name;
    ImageView imageView;
    String regID;


    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;


    EditText et_search;
    ImageView iv_filter;
    TextView tv_title;
    public ImageView getProfilePhotoView(){return imageView;}
    public EditText getKeywordEditText()
    {
        return et_search;
    }
    public ImageView getFilterImageView() { return iv_filter;}
    public TextView getUserNameTextView() { return tv_user_name;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        mContext = this;
        FirebaseApp.initializeApp(mContext);

        if (!PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
            NavigationView navView = findViewById(R.id.nav_view);
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.activity_home_drawer_not_loggin);
        }
        else
        {
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                regID = FirebaseInstanceId.getInstance().getToken();
                Log.d("HomeActivity" , "RegID=" + regID);
                StartAsyncUpdateDeviceId();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
            window.setStatusBarColor(this.getResources().getColor(R.color.colorAccent));
        }
        mProgressView = findViewById(R.id.progress_bar);


        tv_title = (TextView)findViewById(R.id.tv_title);


        //tv_withdrawal.setOnClickListener(this);

        et_search = (EditText) findViewById(R.id.et_search);

        et_search.setNextFocusUpId(R.id.tv_set_location);
        iv_filter = (ImageView)findViewById(R.id.iv_filter_price);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        fragment = HomeFragment.newInstance();
        FragmentManager fragmentManager =  getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment).commit();
        setTitle("Home");
        tv_title.setVisibility(View.GONE);
        tv_user_name = (TextView) navigationView.getHeaderView(navigationView.getHeaderCount() - 1).findViewById(R.id.tv_user_name);
        imageView = (ImageView) navigationView.getHeaderView(navigationView.getHeaderCount() - 1).findViewById(R.id.imageView);


        try {

            JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));

            tv_user_name.setText(profileObj.optString("first_name") + " " + profileObj.optString("last_name"));
            if (profileObj.has("photo")) {
                String url ="";
                String photoString = profileObj.getString("photo");
                if (photoString != null && photoString.compareTo("null") != 0)
                    url = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + profileObj.optString("photo");

                //imageLoader.displayImage(AppUtils.BASE_URL + profileObj.getJSONArray("photo").getJSONObject(0).getString("path"),
                if (!TextUtils.isEmpty(url))
                {
                    Glide.with(mContext).load(url).placeholder(R.drawable.ic_emptyuser).into(imageView);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getIntent().getBooleanExtra(AppUtils.EXTRA.IS_FROM_NOTIFICATION, false)) {
            //startActivity(new Intent(mContext, OrdersActivity.class).putExtras(getIntent().getExtras()));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {



        }
    }

    public void requestPermissionOutside() {
        String[] permissions = new String[]{
                "android.permission.ACCESS_FINE_LOCATION",
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(permissions, permissionRequestId);
    }



    public void setTitle(String str)
    {
        tv_title.setText(str);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        et_search.setVisibility(View.GONE);
        iv_filter.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);
        if (id == R.id.nav_home) {
            fragment = HomeFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            setTitle("Home");

            et_search.setVisibility(View.VISIBLE);
            iv_filter.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.GONE);

        } else if (id == R.id.nav_my_profile) {


            fragment = ProfileFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            setTitle("My Profile");


        } else if (id == R.id.nav_my_product) {
            fragment = MyProductFragment.newInstance();
            //((HomeFragment) fragment).setMyProductFlag(true);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            et_search.setVisibility(View.VISIBLE);
            iv_filter.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.GONE);
            setTitle("My Products");

        }else if (id == R.id.nav_my_chat) {
            fragment = ChatFragment.newInstance();
            //((HomeFragment) fragment).setMyProductFlag(true);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();


            setTitle("My Chat");

        }else if (id == R.id.nav_about_us) {
            fragment = AboutUsFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            setTitle("About Us");

        } else if (id == R.id.nav_logout || id == R.id.nav_login) {
            if (PreferenceManager.getDefaultSharedPreferences(mContext).
                    getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
                AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, "");
                AppUtils.setPreferences(AppUtils.PREFS.USER_TOKEN, mContext, "");
                AppUtils.setPreferences(AppUtils.PREFS.IS_LOGED_IN, mContext, false);


                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            else
            {
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }




        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void StartAsyncUpdateDeviceId() {

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

    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("device_type", 0);
            jObj.put("device_id", regID);
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public void onCustomPostExecuteForTokens(int result, String Message, String ClassName) {
        showProgress(false);
        switch (result) {
            case AppUtils.Async_SUCCESS:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE))
                    onUpdateSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE))
                    onUpdateFailedDesc(Message);
                break;
            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE))
                    onServerUnreachable();
                break;
            default:
                break;
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if( mProgressView == null ) return;
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    protected void onUpdateFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, "");
        AppUtils.setPreferences(AppUtils.PREFS.USER_TOKEN, mContext, "");
        AppUtils.setPreferences(AppUtils.PREFS.IS_LOGED_IN, mContext, false);
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();

    }

    protected void onServerUnreachable() {

        Snackbar.make(mProgressView, getResources().getString(
                R.string.message_server_down), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit");
            alertDialogBuilder
                    .setMessage("Do you want to close?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            HomeActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

    }

    protected void onUpdateSuccess() {
        try {
//            Snackbar.make(mProgressView, getResources().getString(
//                    R.string.message_saved), Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//            AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, mFinalJsonObj.getJSONObject("data").toString());
//            setResult(Activity.RESULT_OK, new Intent());
//            finish();
        } catch (Exception e) {
            e.printStackTrace();
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
}

