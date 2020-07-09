package com.snackspop.snackspopnew.Activity.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
//import android.support.v4.widget.SwipeRefreshLayout;

import com.snackspop.snackspopnew.Activity.AddNewItemActivity;
import com.snackspop.snackspopnew.Activity.FilterActivity;
import com.snackspop.snackspopnew.Activity.LoginActivity;
import com.snackspop.snackspopnew.Activity.SetLocationActivity;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.snackspop.snackspopnew.Activity.Adapter.MyItemsAdapter;
import com.snackspop.snackspopnew.Activity.HomeActivity;
import com.snackspop.snackspopnew.Activity.ItemDetailActivity;
import com.snackspop.snackspopnew.Activity.OthersItemDetailActivity;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Location.LocationUpdateService;
import com.snackspop.snackspopnew.Model.MyItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayoutDirection;
import com.snackspop.snackspopnew.Utils.AppUtils;
import com.snackspop.snackspopnew.Utils.LogCat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    RecyclerView rv_list;
    MyItemsAdapter adapter;
    Context mContext;
    List<MyItemsModelClass> myItemsList;


    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;

    BroadcastReceiver mReceiver;

    EditText et_search;


    public static int requestSetLocationCode = 0x101;

    boolean isSortByPrice = false, isDistanceAccending = true , isPriceAccending = true;
    TextView tv_sort_price, tv_sort_distance , tv_set_location,  tv_add_new;
    protected SharedPreferences LocationSharedPreference;
    private TextView progressMessage;
    TextView noGoodiesNearBy;
    LatLng myServicePositions = null;
    LatLng myCurrentPositions = null;

    BroadcastReceiver mLocationReceiver;
    private boolean isMyProduct = false;

    SwipyRefreshLayout mSwipeRefreshLayout;

    ImageView iv_filter;

    private int limit = 100, page = 1;
    public static int minPriceValue = 0;
    public static int maxPriceValue = 10000;
    int minPrice = minPriceValue, maxPrice = maxPriceValue;
    public static final int locRequestCode = 11;
    public static final int permissionRequestId = 1;
    public static final int permissionRequestIdForSetLocation = 10;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public void setMyProductFlag(boolean flag)
    {
        isMyProduct = flag;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initUi(rootView);
        //---------------------------------------------

        IntentFilter intentFilter = new IntentFilter(
                AppUtils.BROADCAST.BR_UPDATE_MY_ITEMS);
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppUtils.BROADCAST.BR_UPDATE_MY_ITEMS)) {
                    showProgress(true);
                    StartAsyncGetList();

                }
            }
        };
        //mContext.registerReceiver(mReceiver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter(
                AppUtils.BROADCAST.BR_UPDATE_LOCATION);
        mLocationReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppUtils.BROADCAST.BR_UPDATE_LOCATION)) {
                    if( myCurrentPositions == null || myCurrentPositions.latitude == 0 )
                        getData();

                }
            }
        };
        mContext.registerReceiver(mLocationReceiver, intentFilter1);


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mLocationReceiver);
    }

    private void initUi(View rootView) {
        progressMessage = (TextView)rootView.findViewById(R.id.progressMessage);
        noGoodiesNearBy = (TextView)rootView.findViewById(R.id.no_goodies_nearby);
        rv_list = (RecyclerView) rootView.findViewById(R.id.rv_list);
        mProgressView = rootView.findViewById(R.id.progress_bar);
        myItemsList = new ArrayList<>();
        adapter = new MyItemsAdapter(getActivity(), myItemsList);
        adapter.setIsMyProducts(isMyProduct);
        rv_list.setLayoutManager(new LinearLayoutManager(mContext));
        adapter.setmRecyclerView(rv_list);
        rv_list.setAdapter(adapter);

        iv_filter = ((HomeActivity) mContext).getFilterImageView();

        tv_set_location = (TextView) rootView.findViewById(R.id.tv_set_location);
        tv_add_new = (TextView) rootView.findViewById(R.id.tv_add_new);
        tv_set_location.setOnClickListener(this);
        tv_add_new.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipyRefreshLayout)rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);



        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    page = 1;
                    Log.e("//", "//  + direction == TOP");
                } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    Log.e("//", "//  + direction == BOTTOM");
                    page += 1;
                }
                StartAsyncGetList();

            }
        });
        adapter.setOnLoadMoreListener(new MyItemsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add loading item
                myItemsList.add(null);
                adapter.notifyItemInserted(myItemsList.size() - 1);
//                StartAsyncGetList();

            }
        });
        adapter.setmOnRecyclerItemClickListener(new MyItemsAdapter.OnEventsRecyclerItemClick() {
            @Override
            public void onItemClick(View View) {

                switch (View.getId()) {
                    case R.id.rl_main:
                        {
                            MyItemsModelClass model = myItemsList.get((int) View.getTag());

                             startActivity(new Intent(mContext, OthersItemDetailActivity.class).putExtra(AppUtils.EXTRA.MY_ITEM, model));


                        }

                        break;
                }

            }
        });
        showProgress(true);

        /*page = 1;

        StartAsyncGetList();*/

        boolean isSortByPrice = false, isAccending = true;
        et_search = ((HomeActivity) mContext).getKeywordEditText();

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_search.getText().length() > 2) {
                    page = 1;
                    StartAsyncGetList();
                }
                else if (et_search.getText().length() == 0) {
                    page = 1;
                    StartAsyncGetList();
                }
            }
        });

        tv_sort_price = (TextView)rootView.findViewById(R.id.tv_sort_price);
        tv_sort_distance =(TextView)rootView.findViewById(R.id.tv_sort_distance);


        if (tv_sort_price != null)
        {
            tv_sort_price.setOnClickListener(this);
        }
        if (tv_sort_distance != null)
        {
            tv_sort_distance.setOnClickListener(this);
        }

        if (iv_filter != null)
        {
            iv_filter.setOnClickListener(this);
            iv_filter.setVisibility(View.GONE);
        }
        isDistanceAccending = true;

        isSortByPrice = false;
        if (isDistanceAccending)
            tv_sort_distance.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_down, 0, 0, 0);
        else
            tv_sort_distance.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_up, 0, 0, 0);

        getData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private int filterCode = 1011;
    boolean isFilterApplied = false;
    private boolean includeClosed = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == filterCode) {
            if (resultCode == RESULT_OK) {
                isFilterApplied = true;
                includeClosed = data.getBooleanExtra(AppUtils.EXTRA.INCLUDE_CLOSED, true);
                maxPrice =data.getIntExtra(AppUtils.EXTRA.MAX_VALUE_SET, maxPriceValue);
                minPrice = data.getIntExtra(AppUtils.EXTRA.MIN_VALUE_SET, minPriceValue);
                page = 1;
                StartAsyncGetList();
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
        else if (requestCode == locRequestCode) {
            if (resultCode == RESULT_OK) {
                myCurrentPositions = new LatLng(data.getDoubleExtra(AppUtils.EXTRA.LOC_LAT, 0), data.getDoubleExtra(AppUtils.EXTRA.LOC_LNG, 0));
                showProgress(true);
                page = 1;
                StartAsyncGetList();
            }
        }
        else if (requestCode == requestSetLocationCode)
        {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_filter_price:
                if( maxPrice == 0 && minPrice == 0 ){
                    Toast.makeText(getActivity(), "Please wait to get products", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(mContext, FilterActivity.class).putExtra(AppUtils.EXTRA.MIN, minPrice)
                        .putExtra(AppUtils.EXTRA.MAX, maxPrice).putExtra(AppUtils.EXTRA.INCLUDE_CLOSED, includeClosed), filterCode);
                break;
            case R.id.tv_sort_price:
                if (mProgressView.getVisibility() != View.VISIBLE) {
                    isSortByPrice = true;
                    isPriceAccending = !isPriceAccending;
                    tv_sort_distance.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                    if (isPriceAccending)
                        tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_up, 0, 0, 0);
                    else
                        tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_down, 0, 0, 0);

                    page = 1;
                    //getData();
                    StartAsyncGetList();
                }

                break;
            case R.id.tv_sort_distance:
                if (mProgressView.getVisibility() != View.VISIBLE) {
                    isSortByPrice = false;
                    isDistanceAccending = !isDistanceAccending;
                    tv_sort_price.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                    if (isDistanceAccending)
                        tv_sort_distance.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_down, 0, 0, 0);
                    else
                        tv_sort_distance.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_up, 0, 0, 0);
                    page = 1;
                    //getData();
                    StartAsyncGetList();
                }
                break;
            case R.id.tv_set_location:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // only for gingerbread and newer versions
                    if ((ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)) {
                        mContext.startService(new Intent(mContext, LocationUpdateService.class));
                        startActivityForResult(new Intent(mContext, SetLocationActivity.class) ,locRequestCode);

                    } else {
                        requestPermissionOutside();
                    }
                } else {
                    mContext.startService(new Intent(mContext, LocationUpdateService.class));
                    startActivityForResult(new Intent(mContext, SetLocationActivity.class) , locRequestCode );

                }
                break;
            case R.id.tv_add_new:
                if (PreferenceManager.getDefaultSharedPreferences(mContext).
                        getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {

                    try{
                        JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));

                        double geo_lat = Double.parseDouble(profileObj.optString("geo_lat"));
                        double geo_lng = Double.parseDouble(profileObj.optString("geo_lng"));

                        myCurrentPositions = new LatLng(geo_lat,geo_lng);
                        startActivity(new Intent(mContext, AddNewItemActivity.class));

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                        alertDialogBuilder.setTitle("Set Location");
                        alertDialogBuilder
                                .setMessage("You must set location first. Now Set Location?")
                                .setCancelable(false)
                                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        startActivity(new Intent(mContext, SetLocationActivity.class));
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
                else
                {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }

                break;

        }
    }

    private void getData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for gingerbread and newer versions
            if ((ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)) {

                mContext.startService(new Intent(mContext, LocationUpdateService.class));
                showProgress(true);
                if(getCurrentLatLng()==null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getData();
                        }
                    }, 5000);
                }else{
                    //TODO
                    /*if (PreferenceManager.getDefaultSharedPreferences(mContext).
                            getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
                        StartAsyncUpdateLocation();
                    }
                    else*/
                        StartAsyncGetList();
                }

            } else {
                requestPermissionOutside();
            }
        } else {
            mContext.startService(new Intent(mContext, LocationUpdateService.class));
            showProgress(true);
            if(getCurrentLatLng()==null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 5000);
            }else{
                StartAsyncGetList();
            }
        }

    }
    private void StartAsyncUpdateLocation()
    {
        showProgress(true,"Updating Location");
        noGoodiesNearBy.setVisibility(View.GONE);
        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }
        if (myCurrentPositions == null) {
            if (LocationSharedPreference == null){
                LocationSharedPreference = ((Activity) mContext).getApplication().getSharedPreferences(LocationUpdateService.PREF_NAME, MODE_PRIVATE);
            }

            myCurrentPositions = new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LAT, "0")),
                    Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LONG, "0")));
        }

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePairsForUpdateLocation();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.UPDATE_LOCATION,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        isPut = true;
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPDATE ;
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
                try {
                    Snackbar.make(mProgressView, getActivity().getResources().getString(
                            R.string.message_server_down), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }catch(Exception e){
                    Toast.makeText(getActivity(), getActivity().getResources().getString(
                            R.string.message_server_down), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            showProgress(false);
            try {
                Snackbar.make(mProgressView, getActivity().getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }catch(Exception e){
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
            }
        }
    }
    private LatLng getCurrentLatLng(){

        LatLng mCurrentLang = null;
        if (LocationSharedPreference == null){
            LocationSharedPreference = ((Activity) mContext).getApplication().getSharedPreferences(LocationUpdateService.PREF_NAME, MODE_PRIVATE);
        }
        if(LocationSharedPreference.contains(LocationUpdateService.LOCATION_SERVICE_LAT)){
            mCurrentLang =  new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LAT, "0")),
                    Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_SERVICE_LONG, "0")));
            myServicePositions = mCurrentLang;
            return mCurrentLang;
        }else{
            return null;
        }
    }

    public void requestPermissionOutside() {

        String[] permissions = null;


        permissions = new String[]{
                "android.permission.ACCESS_FINE_LOCATION",
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null)
            requestPermissions(permissions, permissionRequestId);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case permissionRequestId: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] ==
                            PackageManager.PERMISSION_GRANTED) {
                        LogCat.e("permission granted ", i + "");
                        if (i == (permissions.length - 1)) {
                            mContext.startService(new Intent(mContext, LocationUpdateService.class));
                            showProgress(true);
                            if(getCurrentLatLng()==null){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getData();
                                    }
                                }, 5000);
                            }else{
                                StartAsyncGetList();
                            }


                        }
                    } else {
                        LogCat.e("permission not granted ", i + "");
                        break;
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }
                return;
            }
            case permissionRequestIdForSetLocation: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] ==
                            PackageManager.PERMISSION_GRANTED) {
                        LogCat.e("permission granted ", i + "");
                        if (i == (permissions.length - 1)) {

                            startActivityForResult(new Intent(mContext, SetLocationActivity.class).putExtra(AppUtils.EXTRA.LOC_LAT, myCurrentPositions.latitude)
                                    .putExtra(AppUtils.EXTRA.LOC_LNG, myCurrentPositions.longitude), locRequestCode);

                        }
                    } else {
                        LogCat.e("permission not granted ", i + "");
                        break;
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }
                return;
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */


    private void showProgress(final boolean show) {
        try {
            showProgress(show,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgress(final boolean show ,String msg){
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            progressMessage.setVisibility(show ? View.VISIBLE : View.GONE);
            if(msg!=null && !msg.isEmpty())progressMessage.setText(msg);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void StartAsyncGetList() {

        showProgress(true,"finding tasty goodies...");
        noGoodiesNearBy.setVisibility(View.GONE);
        //showProgressWithMessage(true,"finding tasty goodies...");
        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }
        if (myCurrentPositions == null) {


            if (LocationSharedPreference == null){
                LocationSharedPreference = ((Activity) mContext).getApplication().getSharedPreferences(LocationUpdateService.PREF_NAME, MODE_PRIVATE);
            }

            myCurrentPositions = new LatLng(Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_LAT, "0")),
                    Double.parseDouble(LocationSharedPreference.getString(LocationUpdateService.LOCATION_LONG, "0")));

            if (myCurrentPositions.latitude == 0 )
            {
                if (myServicePositions.latitude != 0)
                {
                    myCurrentPositions =myServicePositions;
                }
            }
        }

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
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.GET_ITEM_LIST ;
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
                try {
                    Snackbar.make(mProgressView, getActivity().getResources().getString(
                            R.string.message_server_down), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }catch(Exception e){
                    Toast.makeText(getActivity(), getActivity().getResources().getString(
                            R.string.message_server_down), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            showProgress(false);
            try {
                Snackbar.make(mProgressView, getActivity().getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }catch(Exception e){
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onCustomPostExecuteForTokens(int result, String Message, String ClassName) {
        showProgress(false);
        switch (result) {
            case AppUtils.Async_SUCCESS:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.GET_ITEM_LIST))
                    onGetListSuccess();
                else if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE_LOCATION))
                    onUpdateLocationSucess();
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
        try {
            Snackbar.make(mProgressView, getActivity().getResources().getString(
                    R.string.message_server_down), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(mContext, mContext.getResources().getString(
                    R.string.message_server_down), Toast.LENGTH_LONG).show();
        }
    }

    protected void onGetListFailedDesc(String Message) {
        try {
            Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


        }catch (Exception e){
            Toast.makeText(mContext, Message, Toast.LENGTH_LONG).show();
        }
    }

    protected void onGetListSuccess() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    // Stop animation (This will be after 3 seconds)
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 12);

            JSONArray itemArray = mFinalJsonObj.getJSONArray("data");
            if (page == 1)
                myItemsList.clear();
            int length = itemArray.length();
            if (length == 0)
                page -= 1;

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
                items.setPrice_unit(itemArray.getJSONObject(i).getString("item_unit"));
                items.setId(itemArray.getJSONObject(i).getString("id"));
                JSONObject obj = itemArray.getJSONObject(i).getJSONObject("user");
                double distance = -1;
                if (obj.getString("distance") != "null")
                    distance = obj.getDouble("distance");
                items.setDistance(distance);

                items.setUserId(itemArray.getJSONObject(i).getInt("user_id"));
                items.setUnit("km."); //TODO

                //items.setUnit(itemArray.getJSONObject(i).getString("item_price"));
                //items.setOutOfStock(itemArray.getJSONObject(i).optBoolean("isOutOfStock"));
                myItemsList.add(items);
            }

//            if (isSortByPrice) {
//
//            } else {
//                if (isDistanceAccending)
//                    Collections.sort(myItemsList, new Comparator<MyItemsModelClass>() {
//                        @Override
//                        public int compare(MyItemsModelClass lhs, MyItemsModelClass rhs) {
//                            if( lhs.getDistance() < rhs.getDistance() )
//                                return -1;
//                            else if( lhs.getDistance() > rhs.getDistance() )
//                                return 1;
//                            else
//                                return 0;
//                        }
//                    });
//                else
//                    Collections.sort(myItemsList, new Comparator<MyItemsModelClass>() {
//                        @Override
//                        public int compare(MyItemsModelClass lhs, MyItemsModelClass rhs) {
//                            if( lhs.getDistance() < rhs.getDistance() )
//                                return 1;
//                            else if( lhs.getDistance() > rhs.getDistance() )
//                                return -1;
//                            else
//                                return 0;
//                        }
//                    });
//            }

            if(myItemsList==null || myItemsList.isEmpty()){
                Log.i("Show no goodies","Show no goodies");
                noGoodiesNearBy.setVisibility(View.VISIBLE);
            }else{
                Log.i("Show goodies","Show goodies");
                noGoodiesNearBy.setVisibility(View.INVISIBLE);
            }

            adapter.setTotalItemCount(itemArray.length());
            adapter.setVisibleThreshold(itemArray.length());
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onUpdateLocationSucess()
    {
        showProgress(false);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                // Stop animation (This will be after 3 seconds)
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 12);

        page = 1;
        StartAsyncGetList();
    }
    private String getNameValuePairsForUpdateLocation()
    {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("geo_lat" , myCurrentPositions.latitude);
            jObj.put("geo_lng" , myCurrentPositions.longitude);
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            HomeActivity activity =(HomeActivity) mContext;
            EditText etSearch = activity.getKeywordEditText();
            String keyword = etSearch.getText().toString();
            jObj.put("keyword" , keyword);
            jObj.put("sort_type" , isSortByPrice ? 0 : 1);
            if (maxPrice >= 0 && minPrice >=0)
            {
                jObj.put("price_from" , minPrice);
                jObj.put("price_to" , maxPrice);
            }
            boolean direction_type = false;
            direction_type = isSortByPrice ? isPriceAccending : isDistanceAccending;
            jObj.put("direction_type" ,  direction_type ? 1 : 0);
            jObj.put("page" , page);
            jObj.put("limit" , limit);
            jObj.put("user_id" , isMyProduct ? 0 : 1);
            jObj.put("limit" , limit);

            jObj.put("geo_lat" , myCurrentPositions.latitude);
            jObj.put("geo_lng" , myCurrentPositions.longitude);
            int me_user_id = -1;
            if (PreferenceManager.getDefaultSharedPreferences(mContext).
                    getBoolean(AppUtils.PREFS.IS_LOGED_IN, false)) {
                String str_user_id = AppUtils.getUserId(mContext);
                if (!TextUtils.isEmpty(str_user_id ))
                    me_user_id = Integer.parseInt(str_user_id);
            }
            jObj.put("me_user_id" , me_user_id);
//            jObj.put("name", et_product_name.getText().toString());
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

}
