package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_email;
    Button bt_send_verification;

    Context mContext;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_forgot_password);
        initUi();
    }

    private void initUi() {
        mProgressView = findViewById(R.id.progress_bar);
        et_email = (EditText) findViewById(R.id.et_email);
        bt_send_verification = (Button) findViewById(R.id.bt_send_verification);
        bt_send_verification.setOnClickListener(this);
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

    private void StartAsyncSendVerification() {

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.SEND_VERIFICATION,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.SEND_VERIFICATION;
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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.SEND_VERIFICATION))
                    onLoginSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.SEND_VERIFICATION))
                    onLoginFailedDesc(Message);
                break;
            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.SEND_VERIFICATION))
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
            //startActivity(new Intent(mContext, VerificationActivity.class).putExtra(AppUtils.EXTRA.EMAIL,et_email.getText().toString()));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("email", et_email.getText().toString());
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send_verification:
                if (TextUtils.isEmpty(et_email.getText().toString())) {
                    et_email.setError(getResources().getString(R.string.message_required_field));
                    et_email.requestFocus();
                } else {
                    showProgress(true);
                    StartAsyncSendVerification();
                }
                break;
            case R.id.iv_back:
                super.onBackPressed();
                break;
        }
    }
}
