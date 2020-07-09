package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.snackspop.snackspopnew.Activity.Adapter.MyChatItemAdapter;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Model.MyChatItemModel;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayout;
import com.snackspop.snackspopnew.SwipeFreshLayout.SwipyRefreshLayoutDirection;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChattingActivity extends AppCompatActivity implements View.OnClickListener {


    private int userid;
    private String username, userImageUrl;
    private Context mContext;
    private int my_userid , item_id;
    private String my_username, my_userImageUrl;

    private TextView tv_title;

    private EditText m_editText;
    private Button   m_sendButton;
    private RecyclerView m_listView;
    private ProgressBar mProgressView;
    private MyChatItemAdapter itemAdapter;


    private List<MyChatItemModel> m_itemModelList;

    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;
    SwipyRefreshLayout mSwipeRefreshLayout;

    boolean m_top_refreshed = false;
    int page = 1;

    private Socket socket;
    {
        try {
            socket = IO.socket(AppUtils.BASE_SITE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectSocket();
    }

    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("fromUserId", userid);
            jObj.put("toUserId", my_userid);
            jObj.put("itemId", item_id);
            jObj.put("page" , page);

            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public void StartAsyncGetMessages()
    {
        showProgress(true);
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.GET_CHAT_MESSAGES,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.GET_CHAT_MESSAGE_LIST ;
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
            }
        }
        else {
            showProgress(false);
            try {
                Snackbar.make(mProgressView, getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } catch (Exception e) {
                Toast.makeText(this, this.getResources().getString(R.string.message_no_internet), Toast.LENGTH_LONG).show();
            }
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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.GET_CHAT_MESSAGES))
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
    protected void onGetListSuccess() {
        showProgress(false);
        try {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    // Stop animation (This will be after 3 seconds)
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 12);

            if (page == 1)
                m_itemModelList.clear();
            JSONArray cItemArray = mFinalJsonObj.getJSONArray("data");
            JSONArray deliverParam = new JSONArray();

            int count = cItemArray.length();
            if (count == 0)
                page -= 1;
            for (int i = 0 ; i < count ; i++)
            {
                JSONObject object = cItemArray.getJSONObject(i);
                addChatItem(object,false);

                int deliver = object.getInt("deliver");
                int toUserId = object.optInt("to_user_id");
                if (deliver == 0 && toUserId == my_userid)
                {
                    JSONObject paramObj = new JSONObject();
                    paramObj.put("chat_id",object.getInt("id"));
                    deliverParam.put(paramObj);
                }

            }

            m_top_refreshed = false;
            socket.emit("deliver" , deliverParam);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onGetListFailedDesc(String Message) {
        try {
            if (Message.equals("Failed to authenticate token"))
            {
                Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                startActivity(new Intent(this,LoginActivity.class));
            }


        }catch (Exception e){
            Toast.makeText(mContext, Message, Toast.LENGTH_LONG).show();
        }
    }
    protected void onServerUnreachable() {
        try {
            Snackbar.make(mProgressView, getResources().getString(
                    R.string.message_server_down), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e){
            Toast.makeText(mContext, mContext.getResources().getString(
                    R.string.message_server_down), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        Bundle extras = getIntent().getExtras();

        if (getIntent().hasExtra(AppUtils.EXTRA.chatting_user_id)) {
            userid = extras.getInt(AppUtils.EXTRA.chatting_user_id);
        }

        if (getIntent().hasExtra(AppUtils.EXTRA.chatting_user_name)) {
            username = extras.getString(AppUtils.EXTRA.chatting_user_name);
        }

        if (getIntent().hasExtra(AppUtils.EXTRA.chatting_user_id)) {
            userImageUrl = extras.getString(AppUtils.EXTRA.chatting_user_image);
        }

        if (getIntent().hasExtra(AppUtils.EXTRA.chatting_item_id)) {
            item_id = extras.getInt(AppUtils.EXTRA.chatting_item_id);
        }


        initUi();

        showProgress(true);

        try{
            socket.on("connect", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        JSONObject objParam = new JSONObject();
                        objParam.put("userId", my_userid);
                        objParam.put("itemId", item_id);
                        objParam.put("toUserId", userid);
                        socket.emit("join", objParam);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on("joined", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                page = 1;
                                StartAsyncGetMessages();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on("receive", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONObject message = (JSONObject) args[0];
                    try{
                        JSONArray array = new JSONArray();
                        JSONObject obj = new JSONObject();
                        obj.put("chat_id" , message.getString("id"));
                        array.put(obj);
                        socket.emit("deliver" , array);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addChatItem(message , true);
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            });



            socket.on("sent", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONObject obj = (JSONObject)args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                showProgress(false);
                                addChatItem(obj , true);
                                m_editText.setText("");
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            socket.connect();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initUi() {

        mSwipeRefreshLayout = (SwipyRefreshLayout)findViewById(R.id.chatting_swipy_layout);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    page += 1;
                    Log.e("//", "//  + direction == TOP");
                } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    Log.e("//", "//  + direction == BOTTOM");
                    page = 1;
                }
                m_top_refreshed = true;
                StartAsyncGetMessages();

            }
        });

        m_editText = (EditText) findViewById(R.id.editText);
        m_sendButton = (Button) findViewById(R.id.btn_send);
        m_listView = (RecyclerView)findViewById(R.id.chat_list_view);

        m_itemModelList = new ArrayList<MyChatItemModel>();

        itemAdapter = new MyChatItemAdapter(m_itemModelList);
        itemAdapter.setContext(this);
        mProgressView = findViewById(R.id.chatting_progress);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(username);
        m_listView.setAdapter(itemAdapter);
        m_listView.setLayoutManager(new LinearLayoutManager(this));

        if (m_sendButton != null)
            m_sendButton.setOnClickListener(this);

        if (m_editText != null)
            m_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return false;
                }
            });
        try{
            JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, this));
            my_userid = profileObj.optInt("id");
            my_username = profileObj.optString("first_name") + " " + profileObj.optString("last_name");
            String photoid = profileObj.optString("photo");
            String url = "";
            if (photoid != null && photoid.compareTo("null") != 0)
                url =  AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + photoid;
            my_userImageUrl = url;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        findViewById(R.id.iv_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send:
                String message = m_editText.getText().toString();
                if (!TextUtils.isEmpty(message))
                    sendMessage(message);
                break;
            case R.id.iv_back:
                if (AppUtils.chatting_flag) {
                    startActivity(new Intent(this, HomeActivity.class));
                    AppUtils.chatting_flag = false;
                }
                else
                    setResult(Activity.RESULT_CANCELED, new Intent().putExtra(AppUtils.EXTRA.FILTER_IS_CLEARED, true));
                finish();
                break;
        }

    }

    private void addChatItem(JSONObject object ,boolean isLast)
    {
        try
        {
            MyChatItemModel model = new MyChatItemModel();
            model.setUser_id(userid);
            model.setImage1Url(my_userImageUrl);
            model.setImage2Url(userImageUrl);
            int direction = 0;
            int fromUserId =object.getInt("from_user_id");
            int toUserId = object.getInt("to_user_id");
            if (fromUserId == my_userid)
                direction = 0;
            else if (fromUserId == userid)
                direction = 1;
            model.setDirection(direction);
            String dateString = object.getString("createdAt");
            Date mDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).parse(dateString);
            model.setDate(mDate);
            String message = object.getString("message");
            model.setMessage(message);
            model.setObject(object);

            if (isLast)
                m_itemModelList.add(model);
            else
                m_itemModelList.add(0,model);
            itemAdapter.notifyDataSetChanged();
            if (!m_top_refreshed) {
                int pos = m_itemModelList.size() - 1;
                m_listView.scrollToPosition(pos);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String message)
    {
        //addChatItem(0,message,0,new Date());

        try
        {
            JSONObject objParam = new JSONObject();
            objParam.put("fromUserId", my_userid);
            objParam.put("toUserId", userid);
            objParam.put("itemId", item_id);
            objParam.put("message" , message);
            socket.emit("send" ,  objParam);
            showProgress(true);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (AppUtils.chatting_flag) {
            startActivity(new Intent(this, HomeActivity.class));
            AppUtils.chatting_flag = false;
        }
        else
            setResult(Activity.RESULT_CANCELED, new Intent().putExtra(AppUtils.EXTRA.FILTER_IS_CLEARED, true));


        finish();
    }

    private void disconnectSocket(){
        //socket.disconnect();
        try{
            if (socket.connected()) {
                socket.disconnect();
                socket.close();
                /*socket.off("joined");
                socket.off("receive");
                socket.off("messages");
                socket.off("joined");*/
                //        socket = null;

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        disconnectSocket();

    }


}
