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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.snackspop.snackspopnew.Activity.Adapter.MyProductsAdapter;
import com.snackspop.snackspopnew.Activity.AddNewItemActivity;
import com.snackspop.snackspopnew.Activity.FilterActivity;
import com.snackspop.snackspopnew.Activity.HomeActivity;
import com.snackspop.snackspopnew.Activity.ItemDetailActivity;
import com.snackspop.snackspopnew.Activity.SetLocationActivity;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Location.LocationUpdateService;
import com.snackspop.snackspopnew.Model.MyItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayout;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayoutDirection;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class MyProductFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    RecyclerView rv_list;
    MyProductsAdapter adapter;
    Context mContext;
    List<MyItemsModelClass> myItemsList;
    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;

    protected SharedPreferences LocationSharedPreference;
    private TextView progressMessage;
    TextView noGoodiesNearBy , tv_set_location,  tv_add_new;
    LatLng myCurrentPositions = null;
    ImageView iv_filter;
    SwipyRefreshLayout mSwipeRefreshLayout;

    private int limit = 100, page = 1;
    public static int minPriceValue = 0;
    public static int maxPriceValue = 10000;
    int minPrice = minPriceValue, maxPrice = maxPriceValue;
    private boolean includeClosed = true;



    BroadcastReceiver mReceiver;
    EditText et_search;

    public MyProductFragment() {
    }

    public static MyProductFragment newInstance( ) {
        return  new MyProductFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;

        StartAsyncGetList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        View rootView = inflater.inflate(R.layout.fragment_myitems_list, container, false);
        initUi(rootView);

        IntentFilter intentFilter = new IntentFilter(
                AppUtils.BROADCAST.BR_UPDATE_MY_ITEMS);
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppUtils.BROADCAST.BR_UPDATE_MY_ITEMS)) {
                    showProgress(true);
                    page = 1;
                    StartAsyncGetList();

                }
            }
        };
        mContext.registerReceiver(mReceiver, intentFilter);
        return rootView;
    }
    private void initUi(View rootView) {
        progressMessage = (TextView)rootView.findViewById(R.id.progressMessage);
        noGoodiesNearBy = (TextView)rootView.findViewById(R.id.no_goodies_nearby);
        rv_list = (RecyclerView) rootView.findViewById(R.id.rv_list);
        mProgressView = rootView.findViewById(R.id.progress_bar);
        myItemsList = new ArrayList<>();
        tv_add_new = rootView.findViewById(R.id.tv_add_new);
        tv_set_location = rootView.findViewById(R.id.tv_set_location);
        tv_add_new.setOnClickListener(this);
        tv_set_location.setOnClickListener(this);

        adapter = new MyProductsAdapter(myItemsList, this);

        rv_list.setLayoutManager(new LinearLayoutManager(mContext));
        adapter.setmRecyclerView(rv_list);
        rv_list.setAdapter(adapter);

        iv_filter = ((HomeActivity) mContext).getFilterImageView();


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

        adapter.setmOnRecyclerItemClickListener(new MyProductsAdapter.OnEventsRecyclerItemClick(){
            @Override
            public void onItemClick(View view) {

                switch (view.getId()) {
                    case R.id.rl_main:
                    {
                        MyItemsModelClass model = myItemsList.get((int) view.getTag());
                        startActivity(new Intent(mContext, ItemDetailActivity.class).putExtra(AppUtils.EXTRA.MY_ITEM, model));
                    }

                    break;
                }

            }
        });

        adapter.setmOnEventRecyclerItemRemoveBtnClick(new MyProductsAdapter.OnEventRecyclerItemRemoveBtnClick() {
            @Override
            public void onItemRemoveClick(View view) {
                try
                {
                    MyItemsModelClass model = myItemsList.get((int) view.getTag());
                    final int mID = Integer.parseInt(model.getId());

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder
                            .setMessage("Do you want to remove?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    StartAsyncRemoveItem(mID);
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
                catch (Exception e)
                {
                    e.printStackTrace();

                }

            }
        });
        showProgress(true);



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
                    StartAsyncGetList();
                }
                else if (et_search.getText().length() == 0) {
                    StartAsyncGetList();
                }
            }
        });



        if (iv_filter != null)
        {
            iv_filter.setOnClickListener(this);
            iv_filter.setVisibility(View.GONE);
        }

    }
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

    private void StartAsyncRemoveItem(final int id)
    {
        showProgress(true,"Removing item");
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }
        if (AppUtils.haveNetworkConnection(mContext)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.REMOVE_ITEM,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        isDelete = true;
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.DEL_ITEM + id ;
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
            showProgress(false);
            try {
                Snackbar.make(mProgressView, getActivity().getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }catch(Exception e){
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
            }
        }
        showProgress(false);
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
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.GET_MY_ITEM_LIST,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.GET_MY_ITEM_LIST ;
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
    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            HomeActivity activity =(HomeActivity) mContext;
            EditText etSearch = activity.getKeywordEditText();
            String keyword = etSearch.getText().toString();
            jObj.put("keyword" , keyword);
            jObj.put("sort_type" , 0);
            if (maxPrice >= 0 && minPrice >=0)
            {
                jObj.put("price_from" , minPrice);
                jObj.put("price_to" , maxPrice);
            }
            jObj.put("direction_type" ,  1);
            jObj.put("page" , page);
            jObj.put("limit" , limit);
            jObj.put("user_id" , 0);
//            jObj.put("name", et_product_name.getText().toString());
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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.GET_MY_ITEM_LIST))
                    onGetListSuccess();
                else if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.REMOVE_ITEM))
                    onRemoveItemSuccess();
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
            Toast.makeText(getActivity(), getActivity().getResources().getString(
                    R.string.message_server_down), Toast.LENGTH_LONG).show();
        }
    }

    protected void onGetListFailedDesc(String Message) {
        try {
            Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
        }
    }

    protected void onRemoveItemSuccess()
    {
        page = 1;
        StartAsyncGetList();
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
                items.setUserId(itemArray.getJSONObject(i).getInt("user_id"));

                myItemsList.add(items);
            }

            adapter.setTotalItemCount(itemArray.length());
            adapter.setVisibleThreshold(itemArray.length());
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
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
            case R.id.tv_add_new:
                startActivity(new Intent(mContext, AddNewItemActivity.class));
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
        }
    }

    private int filterCode = 1011;
    boolean isFilterApplied = false;
    public static final int locRequestCode = 11;
    public static final int permissionRequestId = 1;

    public void requestPermissionOutside() {

        String[] permissions = null;


        permissions = new String[]{
                "android.permission.ACCESS_FINE_LOCATION",
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null)
            requestPermissions(permissions, permissionRequestId);
    }
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
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
