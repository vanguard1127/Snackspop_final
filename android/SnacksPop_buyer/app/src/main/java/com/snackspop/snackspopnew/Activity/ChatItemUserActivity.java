package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.snackspop.snackspopnew.Activity.Adapter.ChatUserAdapter;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Model.ChatItemsModelClass;
import com.snackspop.snackspopnew.Model.ChatUserModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayout;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayoutDirection;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChatItemUserActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView m_listView;

    private ChatUserAdapter itemAdapter;

    private List<ChatUserModelClass> m_itemModelList;

    private ChatItemsModelClass myItemsModelClass;
    AsyncForAll mAsyncForAll;

    private View mProgressView;
    private TextView progressMessage;
    String nameValuePairsJsonString;
    Context mContext;

    JSONObject mFinalJsonObj;
    SwipyRefreshLayout mSwipeRefreshLayout;

    TextView tv_title;
    private String m_idString;

    private int page = 1;

    BroadcastReceiver m_newMsgReceived;

    @Override
    protected void onResume() {
        super.onResume();

        StartAsyncChatItemWithUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_newMsgReceived != null)
            unregisterReceiver(m_newMsgReceived);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_item);
        mContext = this;
        myItemsModelClass = getIntent().getParcelableExtra(AppUtils.EXTRA.MY_ITEM);

        initUi();

        try{
            m_idString = myItemsModelClass.getId();
            tv_title.setText(myItemsModelClass.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        IntentFilter intentFilter1 = new IntentFilter(
                AppUtils.BROADCAST.BR_NEW_MESSAGE_ARRIVED);
        m_newMsgReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int user_id = -1, item_id = -1;String message = "" , user_name;
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
                for (int i = 0 ; i < m_itemModelList.size(); i++)
                {
                    ChatUserModelClass model = m_itemModelList.get(i);
                    try{
                        int id = model.getUser_id();
                        if (id == user_id)
                        {
                            model.setMessage(message);
                            model.setUnread_count(model.getUnread_count() + 1);

                            itemAdapter.notifyDataSetChanged();
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

        registerReceiver(m_newMsgReceived,intentFilter1);
    }


    private void initUi() {

        mSwipeRefreshLayout = (SwipyRefreshLayout)findViewById(R.id.chat_refresh_layout);
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
                StartAsyncChatItemWithUser();

            }
        });
        m_listView = (RecyclerView)findViewById(R.id.chat_list_view);

        m_itemModelList = new ArrayList<ChatUserModelClass>();

        itemAdapter = new ChatUserAdapter(m_itemModelList);
        itemAdapter.setContext(this);
        m_listView.setAdapter(itemAdapter);
        m_listView.setLayoutManager(new LinearLayoutManager(this));

        progressMessage = (TextView)findViewById(R.id.progressMessage);
        mProgressView = findViewById(R.id.progress_bar);
        tv_title = (TextView)findViewById(R.id.tv_title);
        findViewById(R.id.iv_back).setOnClickListener(this);

        itemAdapter.setmOnRecyclerItemClickListener(new ChatUserAdapter.OnEventsRecyclerItemClick() {
            @Override
            public void onItemClick(View view) {

                switch (view.getId())
                {
                    case R.id.ll_main: {
                        ChatUserModelClass model = m_itemModelList.get((int) view.getTag());

                        String url = model.getUser_image();
                        url = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + url;
                        startActivity(new Intent(mContext, ChattingActivity.class).putExtra(AppUtils.EXTRA.chatting_user_id, model.getUser_id())
                                .putExtra(AppUtils.EXTRA.chatting_user_name, model.getUser_name()).putExtra(AppUtils.EXTRA.chatting_user_image, url).putExtra(AppUtils.EXTRA.chatting_item_id, Integer.parseInt(m_idString)));
                    }
                        break;
                }

            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
    private void StartAsyncChatItemWithUser()
    {
        showProgress(true,"finding messages...");
        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
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
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.GET_CHAT_LIST_WITH_ITEM,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.GET_CHAT_LIST_WITH_ITEM ;
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
                    Snackbar.make(mProgressView, getResources().getString(
                            R.string.message_server_down), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }catch(Exception e){
                    Toast.makeText(this, getResources().getString(
                            R.string.message_server_down), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            showProgress(false);
            try {
                Snackbar.make(mProgressView, getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }catch(Exception e){
                Toast.makeText(this, getResources().getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("item_id", m_idString);
            jObj.put("page" ,page);
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
    protected void onServerUnreachable() {
        try {
            Snackbar.make(mProgressView, getResources().getString(
                    R.string.message_server_down), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(this, getResources().getString(
                    R.string.message_server_down), Toast.LENGTH_LONG).show();
        }
    }

    protected void onGetListFailedDesc(String Message) {
        try {
            Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(this, Message, Toast.LENGTH_LONG).show();
        }
    }

    protected void onGetListSuccess() {
        try {

            JSONArray itemArray = mFinalJsonObj.getJSONArray("data");
            if (page == 1)
                m_itemModelList.clear();
            int length = itemArray.length();
            if (length == 0)
                page -= 1;
            for (int i = 0; i < itemArray.length(); i++) {

                if (page == 1)
                    m_itemModelList.clear();
                ChatUserModelClass items = new ChatUserModelClass();

                JSONObject obj = itemArray.getJSONObject(i);
                String message = "";
                JSONArray jsonmsgArray = obj.getJSONObject("room").getJSONArray("messages");
                if (jsonmsgArray.length() > 0)
                {
                    message = AppUtils.getJSONStringValue(jsonmsgArray.getJSONObject(0),"message");
                }
                else
                {
                    continue;
                }
                items.setMessage(message);

                int unread_count = obj.optInt("unread_cnt");
                String username ,user_image;
                items.setUser_id(obj.getJSONObject("user").getInt("id"));
                username = AppUtils.getJSONStringValue(obj.getJSONObject("user"), "first_name") + " " +
                        AppUtils.getJSONStringValue(obj.getJSONObject("user"),"last_name");
                user_image = AppUtils.getJSONStringValue(obj.getJSONObject("user") , "photo");
                items.setUser_name(username);
                items.setUser_image(user_image);
                items.setUnread_count(unread_count);

                m_itemModelList.add(items);
            }
            itemAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.GET_CHAT_LIST_WITH_ITEM))
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


}
