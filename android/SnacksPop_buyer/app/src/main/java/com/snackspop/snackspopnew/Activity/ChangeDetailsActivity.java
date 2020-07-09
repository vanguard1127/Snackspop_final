package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONObject;

public class ChangeDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_change_details;
    Button bt_change_details;
    TextView tv_title;

    String changeParameter;
    Context mContext;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_change_details);
        changeParameter = getIntent().getStringExtra(AppUtils.EXTRA.CHANGE_PARAMTER);
        initUi();
    }

    private void initUi() {


        mProgressView = findViewById(R.id.progress_bar);
        et_change_details = (EditText) findViewById(R.id.et_change_details);
        bt_change_details = (Button) findViewById(R.id.bt_change_details);
        tv_title = (TextView) findViewById(R.id.tv_title);

        switch (changeParameter) {
            case "first_name":
                tv_title.setText((getResources().getString(R.string.label_update)
                        + " " + getResources().getString(R.string.prompt_first_name)).toUpperCase());
                et_change_details.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                et_change_details.setHint(getResources().getString(R.string.prompt_first_name));
                break;
            case "last_name":
                tv_title.setText((getResources().getString(R.string.label_update)
                        + " " + getResources().getString(R.string.prompt_last_name)).toUpperCase());
                et_change_details.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                et_change_details.setHint(getResources().getString(R.string.prompt_last_name));
                break;
            case "phone_number":
                tv_title.setText((getResources().getString(R.string.label_update)
                        + " " + getResources().getString(R.string.prompt_phone_number)).toUpperCase());
                et_change_details.setInputType(InputType.TYPE_CLASS_PHONE);
                et_change_details.setHint(getResources().getString(R.string.prompt_phone_number));
                break;
        }

        bt_change_details.setOnClickListener(this);
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

    private void StartAsyncUpdate() {

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

    protected void onServerUnreachable() {

        Snackbar.make(mProgressView, getResources().getString(
                R.string.message_server_down), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onUpdateFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onUpdateSuccess() {
        try {
            Snackbar.make(mProgressView, getResources().getString(
                    R.string.message_saved), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, mFinalJsonObj.getJSONObject("user").toString());
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put(changeParameter, et_change_details.getText().toString());
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_change_details:
                showProgress(true);
                StartAsyncUpdate();
                break;
            case R.id.iv_back:
                super.onBackPressed();
                break;
        }
    }
}
