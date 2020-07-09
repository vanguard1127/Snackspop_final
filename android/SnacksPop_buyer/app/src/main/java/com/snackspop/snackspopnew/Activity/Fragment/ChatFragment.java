package com.snackspop.snackspopnew.Activity.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.snackspop.snackspopnew.Activity.Adapter.ChatAdapter;
import com.snackspop.snackspopnew.Activity.ChatItemUserActivity;
import com.snackspop.snackspopnew.Activity.ChattingActivity;
import com.snackspop.snackspopnew.Activity.HomeActivity;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Location.LocationUpdateService;
import com.snackspop.snackspopnew.Model.ChatItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayout;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayoutDirection;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    RecyclerView rv_list ;
    ChatAdapter adapter ;
    Context mContext;
    List<ChatItemsModelClass> myItemsList;


    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;

    protected SharedPreferences LocationSharedPreference;
    private TextView progressMessage;
    TextView noGoodiesNearBy;
    LatLng myCurrentPositions = null;
    ImageView iv_filter;
    SwipyRefreshLayout mSwipeRefreshLayout;

    BroadcastReceiver m_newMsgReceived;
    private int limit = 100, page = 1;

    public ChatFragment() {
    }

    public static ChatFragment newInstance( ) {
        return  new ChatFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_chat_items_list, container, false);
        initUi(rootView);

        IntentFilter intentFilter1 = new IntentFilter(
                AppUtils.BROADCAST.BR_NEW_MESSAGE_ARRIVED);
        m_newMsgReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int user_id , item_id = -1;String message = "" , user_name;
                Bundle extras = intent.getExtras();
                if (intent.hasExtra("user_id"))
                {
                    user_id = extras.getInt("user_id");
                }
                if (intent.hasExtra("user_name"))
                {
                    user_name = extras.getString("user_name");
                }
                if (intent.hasExtra("user_id"))
                {
                    message = extras.getString("message");
                }
                if (intent.hasExtra("item_id"))
                {
                    item_id = extras.getInt("item_id");
                }
                for (int i = 0 ; i < myItemsList.size(); i++)
                {
                    ChatItemsModelClass model = myItemsList.get(i);
                    try{
                        int id = Integer.parseInt(model.getId());
                        if (id == item_id)
                        {
                            model.setMessage(message);
                            model.setChat_unread_count(model.getChat_unread_count() + 1);

                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        continue;
                    }

                }
            }
        };
        mContext.registerReceiver(m_newMsgReceived,intentFilter1);
        return rootView;
    }
    private void initUi(View rootView) {
        progressMessage = (TextView)rootView.findViewById(R.id.progressMessage);
        noGoodiesNearBy = (TextView)rootView.findViewById(R.id.no_goodies_nearby);
        rv_list = (RecyclerView) rootView.findViewById(R.id.rv_list);


        mProgressView = rootView.findViewById(R.id.progress_bar);
        myItemsList = new ArrayList<>();

        adapter = new ChatAdapter(myItemsList, this);

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

        adapter.setmOnRecyclerItemClickListener(new ChatAdapter.OnEventsRecyclerItemClick(){
            @Override
            public void onItemClick(View view) {

                switch (view.getId()) {
                    case R.id.rl_main:
                    {
                        try
                        {
                            ChatItemsModelClass model = myItemsList.get((int) view.getTag());
                            int userid = model.getUserid();
                            int from_user_id = model.getFrom_user_id();
                            int to_user_id = model.getTo_user_id();

                            int myUserId =Integer.parseInt(AppUtils.getUserId(mContext));
                            if (from_user_id != myUserId)
                                to_user_id = from_user_id;
                            int item_id = Integer.parseInt(model.getId());
                            if (userid != myUserId) {

                                String url = model.getUser_image();
                                url = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + url;
                                startActivityForResult(new Intent(mContext, ChattingActivity.class).putExtra(AppUtils.EXTRA.chatting_user_id, to_user_id)
                                        .putExtra(AppUtils.EXTRA.chatting_user_name ,model.getUsername()).putExtra(AppUtils.EXTRA.chatting_user_image , url).putExtra(AppUtils.EXTRA.chatting_item_id , item_id), 1);
                            }
                            else
                                startActivity(new Intent(mContext, ChatItemUserActivity.class).putExtra(AppUtils.EXTRA.MY_ITEM, model));


                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        }
                    break;
                }

            }
        });

        showProgress(true);


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
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.GET_ITEM_LIST_WITH_CHAT,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.GET_ITEM_LIST_WITH_CHAT ;
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

        if (m_newMsgReceived != null)
            mContext.unregisterReceiver(m_newMsgReceived);
    }
    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("page" , page);
            jObj.put("limit" , limit);
            jObj.put("user_id" , 0);

            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
    public void onCustomPostExecuteForTokens(int result, String Message, String ClassName) {
        showProgress(false);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                // Stop animation (This will be after 3 seconds)
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 12);

        switch (result) {
            case AppUtils.Async_SUCCESS:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.GET_ITEM_LIST_WITH_CHAT))
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
        try {
            Snackbar.make(mProgressView, getActivity().getResources().getString(
                    R.string.message_server_down), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(getActivity(), mContext.getResources().getString(
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


    protected void onGetListSuccess() {
        try {

            JSONArray itemArray = mFinalJsonObj.getJSONArray("data");
            if (page == 1)
                myItemsList.clear();

            int length = itemArray.length();
            if (length == 0)
                page -= 1;
            for (int i = 0; i < itemArray.length(); i++) {

                ChatItemsModelClass items = new ChatItemsModelClass();

                JSONObject obj = itemArray.getJSONObject(i);

                int unreadCount = obj.optInt("unread_cnt");


                JSONArray messageJSArray = obj.getJSONObject("room").getJSONArray("messages");
                if (messageJSArray.length() > 0) {
                    String message = AppUtils.getJSONStringValue(messageJSArray.getJSONObject(0)
                            , "message");

                    items.setMessage(message);

                    String dateString = messageJSArray.getJSONObject(0).getString("createdAt");
                    Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).parse(dateString);
                    items.setCreatedDate(date);
                }
                else
                {
                    continue;
                }
                items.setName(AppUtils.getJSONStringValue(obj.getJSONObject("room").getJSONObject("item"),"item_name"));

                if (obj.getJSONObject("room").getJSONObject("item").getJSONArray("images").length() > 0)
                {
                    String url = AppUtils.AFTER_BASE_URL.IMAGE_URL +obj.getJSONObject("room").getJSONObject("item").getJSONArray("images").getJSONObject(0).getString("image");
                    items.setImageUrl(url);
                }
                items.setPrice(obj.getJSONObject("room").getJSONObject("item").getString("item_price"));
                items.setId(obj.getJSONObject("room").getJSONObject("item").getString("id"));
                int item_user_id =obj.getJSONObject("room").getJSONObject("item").getInt("user_id");

                items.setUserid(item_user_id);

                String username = "" ,photo ="";
                username = AppUtils.getJSONStringValue(obj.getJSONObject("room").getJSONObject("item").getJSONObject("user"),"first_name") + " "
                        +AppUtils.getJSONStringValue(obj.getJSONObject("room").getJSONObject("item").getJSONObject("user"),"last_name");
                photo = AppUtils.getJSONStringValue(obj.getJSONObject("room").getJSONObject("item").getJSONObject("user"),"photo");

                items.setUsername(username);
                items.setUser_image(photo);

                int room_from_user_id = obj.getJSONObject("room").getInt("from_user_id");
                int room_to_user_id = obj.getJSONObject("room").getInt("to_user_id");

                items.setFrom_user_id(room_from_user_id);
                items.setTo_user_id(room_to_user_id);

                items.setUnit("km."); //TODO
                items.setId(obj.getJSONObject("room").getJSONObject("item").getString("id"));
                items.setChat_unread_count(unreadCount);


                //replaceOrAdd(items);
                myItemsList.add(items);

            }
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {


        }
    }


    public  void replaceOrAdd(ChatItemsModelClass model)
    {
        boolean isReplaced = false;
        for (int i = 0 ; i < myItemsList.size() ;i++)
        {

            ChatItemsModelClass item = myItemsList.get(i);
            if (item.getId().equals(model.getId()))
            {
                isReplaced = true;
                if (model.getCreatedDate().compareTo(item.getCreatedDate()) > 0) {
                    ChatItemsModelClass mItem = myItemsList.get(i);
                    mItem.setMessage(model.getMessage());
                    myItemsList.set(i,mItem);
                }

            }
        }
        if (!isReplaced)
            myItemsList.add(model);
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
