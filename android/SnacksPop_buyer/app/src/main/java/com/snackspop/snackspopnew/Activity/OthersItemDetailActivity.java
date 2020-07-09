package com.snackspop.snackspopnew.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.snackspop.snackspopnew.Activity.Adapter.MyItemsHorizontalAdapter;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Location.LocationUpdateService;
import com.snackspop.snackspopnew.Model.MyItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OthersItemDetailActivity extends AppCompatActivity implements View.OnClickListener {


    ImageView iv_item_image;
    TextView tv_product_desc, tv_price, tv_product_name, tv_title_item_name;
    TextView tv_seller_name;
    TextView tv_distance ;

    Context mContext;
    MyItemsModelClass myItemsModelClass;

    RecyclerView rv_list_horizontal;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;

    BroadcastReceiver mReceiver;


    List<MyItemsModelClass> myItemsList;
    MyItemsHorizontalAdapter m_horizontalAdapter;



    int user_id , item_id; String phone_number , email; String user_url , username;
    Button bt_call, bt_chat;
    double item_price; String item_price_unit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_item_detail_other);
        myItemsModelClass = getIntent().getParcelableExtra(AppUtils.EXTRA.MY_ITEM);
        initUi();

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppUtils.BROADCAST.BR_ORDER_CREATED)) {
                    finish();

                } else if (intent.getAction().equals(AppUtils.BROADCAST.BR_UPDATE_CART_List)) {

                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(
                AppUtils.BROADCAST.BR_ORDER_CREATED);

        intentFilter.addAction(AppUtils.BROADCAST.BR_UPDATE_CART_List);
        mContext.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    private void initUi() {
        mProgressView = findViewById(R.id.progress_bar);
        iv_item_image = (ImageView) findViewById(R.id.iv_item_image);
        tv_product_desc = (TextView) findViewById(R.id.tv_product_desc);
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_title_item_name = (TextView) findViewById(R.id.tv_title_item_name);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_seller_name = (TextView) findViewById(R.id.tv_seller_name);

        bt_call = (Button) findViewById(R.id.bt_call);
        bt_chat =(Button)findViewById(R.id.bt_chat);

        rv_list_horizontal = (RecyclerView) findViewById(R.id.rv_list_horizontal);
        myItemsList = new ArrayList<>();
        m_horizontalAdapter = new MyItemsHorizontalAdapter(myItemsList, this);
        rv_list_horizontal.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        m_horizontalAdapter.setmRecyclerView(rv_list_horizontal);
        rv_list_horizontal.setAdapter(m_horizontalAdapter);
        m_horizontalAdapter.setmOnRecyclerItemClickListener(new MyItemsHorizontalAdapter.OnEventsRecyclerItemClick() {
            @Override
            public void onItemClick(View View) {

                switch (View.getId()) {
                    case R.id.ll_main:
                        myItemsModelClass = myItemsList.get((int) View.getTag());
                        setdata();
                        break;
                }

            }
        });

        if (bt_call != null)
        {
            bt_call.setOnClickListener(this);
        }
        if (bt_chat != null)
        {
            bt_chat.setOnClickListener(this);
        }
        findViewById(R.id.iv_back).setOnClickListener(this);
        setdata();
        showProgress(true);
        StartAsyncGetList(myItemsModelClass);

    }

    private void setdata() {
        try
        {
            tv_product_desc.setText(getResources().getString(R.string.label_product_description) + " " + myItemsModelClass.getDescription());
            tv_product_name.setText(myItemsModelClass.getName());
            tv_title_item_name.setText(myItemsModelClass.getName());

            tv_price.setTypeface(null, Typeface.BOLD);
            String price_unit = AppUtils.displayCurrencyInfoForLocale(Locale.getDefault(),this);
            if (!TextUtils.isEmpty(myItemsModelClass.getPrice_unit()))
            {
                price_unit = myItemsModelClass.getPrice_unit();
            }
            tv_price.setText(myItemsModelClass.getPrice() + price_unit);

            item_id = Integer.parseInt(myItemsModelClass.getId());
            String url = AppUtils.BASE_URL + myItemsModelClass.getImageUrl();
            Glide.with(mContext).load(url).placeholder(R.drawable.ic_logo_cir_red).into(iv_item_image);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1001;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String uri = "tel:" + phone_number.trim() ;
                    callIntent.setData(Uri.parse(uri));//change the number
                    startActivity(callIntent);
                    // permission was granted, yay! Do the phone call

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                super.onBackPressed();
                break;
            case R.id.bt_call:
                if (PreferenceManager.getDefaultSharedPreferences(mContext).
                        getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
                    try {

                        String myUserIdString = AppUtils.getUserId(this);
                        int myUserId = Integer.parseInt(myUserIdString);

                        if (myUserId == user_id)
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                            alertDialogBuilder.setTitle("Alert");
                            alertDialogBuilder
                                    .setMessage("You cannot message or call yourself.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // if this button is clicked, close
                                            // current activity

                                        }
                                    });


                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();
                        }
                        else
                        {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            String uri = "tel:" + phone_number.trim();
                            callIntent.setData(Uri.parse(uri));//change the number

                            // Here, thisActivity is the current activity
                            if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.CALL_PHONE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

                                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            } else {
                                //You already have permission
                                try {
                                    startActivity(callIntent);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        }



                    } catch (Exception e) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Alert")
                                .setMessage("Invalid Phone Number.")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(R.drawable.ic_logo_cir)
                                .setCancelable(false)
                                .show();
                        e.printStackTrace();
                    }
                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder
                            .setMessage("You must Login first. Now You Want to Login?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    startActivity(new Intent(mContext, LoginActivity.class));
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
                break;
            case R.id.bt_chat:
                Log.d("Chatting" , "UserID = " + user_id);
                Log.d("Chatting" , "ItemID = " + item_id);
                Log.d("Chatting" , "User URL = " + user_url);
                if (PreferenceManager.getDefaultSharedPreferences(mContext).
                        getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {

                    String myUserIdString = AppUtils.getUserId(this);
                    int myUserId = Integer.parseInt(myUserIdString);

                    if (myUserId == user_id)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                        alertDialogBuilder.setTitle("Alert");
                        alertDialogBuilder
                                .setMessage("You cannot message or call yourself.")
                                .setCancelable(false)
                                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity

                                    }
                                });


                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                    else
                    {
                        startActivity(new Intent(mContext, ChattingActivity.class).putExtra(AppUtils.EXTRA.chatting_user_id, user_id)
                                .putExtra(AppUtils.EXTRA.chatting_user_name ,username).putExtra(AppUtils.EXTRA.chatting_user_image , user_url).putExtra(AppUtils.EXTRA.chatting_item_id , item_id));

                    }

                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder
                            .setMessage("You must Login first. Now You Want to Login?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    startActivity(new Intent(mContext, LoginActivity.class));
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
                break;
        }
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

    private void StartAsyncGetList(final MyItemsModelClass currentItem) {

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.GET_ITEM_LIST,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.GET_ITEM + currentItem.getId();
                        accessToken = AppUtils.getAccessToken(mContext);
                        Integer flag = super.doInBackground(params);
                        return flag;
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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.GET_ITEM_LIST))
                    onGetListSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                onGetListFailedDesc(Message);
                break;
            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
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

    protected void onGetListFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onGetListSuccess() {
        try {


            String addressLine1 = mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optString("address_line1");
            String addressLine2 = mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optString("address_line2");
            String address = "";

            if (addressLine1 != null && !addressLine1.equals("null") )
                    address = addressLine1;
            if (addressLine2 != null && !addressLine2.equals("null"))
                    address += "  , " + addressLine2;

            username = mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optString("first_name") + " " +
                    mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optString("last_name");
            tv_seller_name.setText(username);

            phone_number = mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optString("phone_number");
            String photo = mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optString("photo");
            if (photo != null && photo.compareTo("null")!=0)
            {
                user_url = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + photo;
            }
            else
                user_url = "";

            email = mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optString("email");
            user_id = mFinalJsonObj.getJSONObject("data").getJSONObject("item").optInt("user_id");
            DecimalFormat df = new DecimalFormat("#.##");

            item_id = mFinalJsonObj.getJSONObject("data").getJSONObject("item").optInt("id");

            double distance = mFinalJsonObj.getJSONObject("data").getJSONObject("item").getJSONObject("user").optDouble("distance");
            tv_distance.setText(df.format(distance) + "km");


            item_price = mFinalJsonObj.getJSONObject("data").getJSONObject("item").optInt("item_price");
            item_price_unit = AppUtils.getJSONStringValue(mFinalJsonObj.getJSONObject("data").getJSONObject("item") , "item_unit");
            if (TextUtils.isEmpty(item_price_unit))
                item_price_unit = AppUtils.displayCurrencyInfoForLocale(Locale.getDefault(),this);
            tv_price.setText(df.format(item_price) + item_price_unit);


            JSONArray itemArray = mFinalJsonObj.getJSONObject("data").getJSONArray("items");
            myItemsList.clear();
            for (int i = 0; i < itemArray.length(); i++) {
                MyItemsModelClass items = new MyItemsModelClass();

                items.setName(itemArray.getJSONObject(i).getString("item_name"));
                items.setDescription(itemArray.getJSONObject(i).getString("item_description"));
                if (itemArray.getJSONObject(i).getJSONArray("images").length() > 0)
                {
                    String url = AppUtils.AFTER_BASE_URL.IMAGE_URL +itemArray.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("image");
                    items.setImageUrl(url);
                }
                items.setPrice(itemArray.getJSONObject(i).getString("item_price"));
                String unit_ = itemArray.getJSONObject(i).getString("item_unit");
                if (unit_.equals("null"))
                    unit_ = "";
                items.setPrice_unit(unit_);
                items.setId(itemArray.getJSONObject(i).getString("id"));
                items.setDistance(distance);
                items.setUserId(itemArray.getJSONObject(i).getInt("user_id"));
                myItemsList.add(items);

            }
            m_horizontalAdapter.setTotalItemCount(myItemsList.size());
            m_horizontalAdapter.setVisibleThreshold(myItemsList.size());
            m_horizontalAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    LatLng myCurrentPositions = null;

    protected SharedPreferences LocationSharedPreference;
    private String getNameValuePair() {
        try {
            LocationSharedPreference = getApplication().getSharedPreferences(LocationUpdateService.PREF_NAME, MODE_PRIVATE);
            if(LocationSharedPreference.contains(LocationUpdateService.LOCATION_SERVICE_LAT)){
                myCurrentPositions =  new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LAT, "0")),
                        Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LONG, "0")));
            }

            JSONObject jObj = new JSONObject();
            jObj.put("geo_lat" , myCurrentPositions.latitude);
            jObj.put("geo_lng" , myCurrentPositions.longitude);
            //jObj.put("name", et_product_name.getText().toString()); TODO with item id
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}
