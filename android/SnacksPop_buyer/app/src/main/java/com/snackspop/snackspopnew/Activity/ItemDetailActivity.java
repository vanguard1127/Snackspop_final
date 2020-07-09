package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Model.MyItemsModelClass;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONObject;

import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener {


    final int RESULT_CODE_FOR_EDIT = 101;

    ImageView iv_item_image;
    TextView tv_product_desc, tv_price, tv_product_name, tv_title_item_name;
    Button bt_edit;
    TextView tv_setout_of_stock;

    Context mContext;
    MyItemsModelClass myItemsModelClass;


    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_item_detail);
        myItemsModelClass = getIntent().getParcelableExtra(AppUtils.EXTRA.MY_ITEM);
        initUi();
    }

    private void initUi() {
        mProgressView = findViewById(R.id.progress_bar);
        tv_setout_of_stock = (TextView) findViewById(R.id.tv_setout_of_stock);
        iv_item_image = (ImageView) findViewById(R.id.iv_item_image);
        tv_product_desc = (TextView) findViewById(R.id.tv_product_desc);
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_title_item_name = (TextView) findViewById(R.id.tv_title_item_name);
        tv_price = (TextView) findViewById(R.id.tv_price);
        bt_edit = (Button) findViewById(R.id.bt_edit);

        tv_product_desc.setText(getResources().getString(R.string.label_product_description) + " " + myItemsModelClass.getDescription());
        tv_product_name.setText(myItemsModelClass.getName());
        tv_title_item_name.setText(myItemsModelClass.getName());
        //tv_price.setText(AppUtils.displayCurrencyInfoForLocale(Locale.getDefault(),mContext) + myItemsModelClass.getPrice());
        if (TextUtils.isEmpty(myItemsModelClass.getPrice_unit()))
            tv_price.setText(AppUtils.displayCurrencyInfoForLocale(Locale.getDefault(),mContext) + myItemsModelClass.getPrice());
        else
            tv_price.setText(myItemsModelClass.getPrice_unit() + myItemsModelClass.getPrice());

        findViewById(R.id.iv_back).setOnClickListener(this);
        bt_edit.setOnClickListener(this);
        tv_setout_of_stock.setOnClickListener(this);
        tv_setout_of_stock.setVisibility(View.GONE);
        if (myItemsModelClass.isOutOfStock())
            tv_setout_of_stock.setText(getResources().getString(R.string.label_reout_of));
        else
            tv_setout_of_stock.setText(getResources().getString(R.string.label_out_of));


        Glide.with(mContext).load(AppUtils.BASE_URL + myItemsModelClass.getImageUrl()).placeholder(R.drawable.ic_logo_cir_red).into(iv_item_image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_edit:
                startActivityForResult(new Intent(mContext, AddNewItemActivity.class).putExtra(AppUtils.EXTRA.MY_ITEM, myItemsModelClass), RESULT_CODE_FOR_EDIT);
                break;
            case R.id.iv_back:
                super.onBackPressed();
                break;
            case R.id.tv_setout_of_stock:
                if (mProgressView.getVisibility() != View.VISIBLE) {
                    showProgress(true);
                    StartAsyncUpdateOutOfStock();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_FOR_EDIT) {
            if (resultCode == RESULT_OK) {
                super.onBackPressed();
            }
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


    private void StartAsyncUpdateOutOfStock() {

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.CREATE_NEW_ITEM,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPDATE_ITEM + myItemsModelClass.getId();
                        isPut = true;
                        accessToken = AppUtils.getAccessToken(mContext);
                        return super.doInBackground(params);
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        super.onPostExecute(result);

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
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.CREATE_NEW_ITEM))
                    onCreateSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                onCreateFailedDesc(Message);
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

    protected void onCreateFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onCreateSuccess() {
        try {
            myItemsModelClass.setOutOfStock(!myItemsModelClass.isOutOfStock());
            if (myItemsModelClass.isOutOfStock())
                tv_setout_of_stock.setText(getResources().getString(R.string.label_reout_of));
            else
                tv_setout_of_stock.setText(getResources().getString(R.string.label_out_of));
            sendBroadcast(new Intent(AppUtils.BROADCAST.BR_UPDATE_MY_ITEMS));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("isOutOfStock", !myItemsModelClass.isOutOfStock());
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}
