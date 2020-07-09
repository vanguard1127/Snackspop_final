package com.snackspop.snackspopnew.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.snackspop.snackspopnew.Model.NameValuePairs;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddNewItemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTIVITY_IMAGE_CROPPER = 10;
    ImageView iv_item_image;
    TextView et_product_desc, ev_price, et_product_name, tv_title_item_name, tv_currency;
    Button bt_save;

    Context mContext;
    MyItemsModelClass myItemsModelClass;

    JSONArray imageJsoArray;
    boolean isImageChanged = false;


    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_add_new_item);
        if (getIntent().hasExtra(AppUtils.EXTRA.MY_ITEM))
            myItemsModelClass = getIntent().getParcelableExtra(AppUtils.EXTRA.MY_ITEM);

        initUi();
    }

    private void initUi() {
        iv_item_image = (ImageView) findViewById(R.id.iv_item_image);
        et_product_desc = (TextView) findViewById(R.id.et_product_desc);
        et_product_name = (TextView) findViewById(R.id.et_product_name);
        tv_currency = (TextView) findViewById(R.id.tv_currency);
        mProgressView = findViewById(R.id.progress_bar);

        tv_title_item_name = (TextView) findViewById(R.id.tv_title_item_name);
        ev_price = (TextView) findViewById(R.id.ev_price);
        bt_save = (Button) findViewById(R.id.bt_save);

        String price_unit_string = "";
        if (myItemsModelClass != null) {
            et_product_desc.setText(myItemsModelClass.getDescription());
            et_product_name.setText(myItemsModelClass.getName());
            ev_price.setText(myItemsModelClass.getPrice());
            tv_title_item_name.setText(myItemsModelClass.getName().toUpperCase());
            if (!TextUtils.isEmpty(myItemsModelClass.getPrice_unit()))
                price_unit_string = myItemsModelClass.getPrice_unit();
            Glide.with(mContext).load(AppUtils.BASE_URL + myItemsModelClass.getImageUrl()).placeholder(R.drawable.ic_logo_cir_red).into(iv_item_image);


        } else {
            tv_title_item_name.setText(getResources().getString(R.string.label_add_new));
        }


        //
        if (!TextUtils.isEmpty(price_unit_string))
            tv_currency.setText(price_unit_string);
        else
            tv_currency.setText(AppUtils.displayCurrencyInfoForLocale(Locale.getDefault(),mContext));
        //tv_currency.setText("$");

        findViewById(R.id.iv_back).setOnClickListener(this);
        iv_item_image.setOnClickListener(this);
        bt_save.setOnClickListener(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == ACTIVITY_IMAGE_CROPPER) {
            if (resultCode == RESULT_OK) {
                Bitmap bmp = ImageCropperActivity.image;
                isImageChanged = true;
                Glide.with(mContext).load(bmp).into(iv_item_image);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                if (TextUtils.isEmpty(et_product_name.getText().toString())) {
                    et_product_name.setError(getResources().getString(R.string.message_required_field));
                    et_product_name.requestFocus();
                } else if (TextUtils.isEmpty(et_product_desc.getText().toString())) {
                    et_product_desc.setError(getResources().getString(R.string.message_required_field));
                    et_product_desc.requestFocus();
                } else if (TextUtils.isEmpty(ev_price.getText().toString())) {
                    ev_price.setError(getResources().getString(R.string.message_required_field));
                    ev_price.requestFocus();
                } else {
                    showProgress(true);
                    if (myItemsModelClass != null) {
                        if (isImageChanged)
                            StartAsyncUpload(((BitmapDrawable) iv_item_image.getDrawable()).getBitmap());
                        else
                            StartAsyncCreate();
                    } else
                        StartAsyncUpload(((BitmapDrawable) iv_item_image.getDrawable()).getBitmap());

                }
                break;
            case R.id.iv_item_image:
                startActivityForResult(new Intent(mContext, ImageCropperActivity.class), ACTIVITY_IMAGE_CROPPER);
                break;
            case R.id.iv_back:
                super.onBackPressed();
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


    private void StartAsyncCreate() {

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
                        if (myItemsModelClass != null) {
                            urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPDATE_ITEM + "/" + myItemsModelClass.getId();
                            isPut = true;
                        } else
                            urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.CREATE_NEW_ITEM;
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


    private void StartAsyncUpload(Bitmap bmp) {

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }


        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            if (bmp == null)
                return;
            ArrayList<NameValuePairs> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new NameValuePairs("imageupload", bmp, "profile"));

            mAsyncForAll = new AsyncForAll(mContext, nameValuePairs, AppUtils.ASYNC_CLASSNAME.UPLOAD_FILE, getHeaderNameValuePair(),
                    false, true, true) {

                @Override
                protected Integer doInBackground(Void... params) {
                    urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPLOAD_FILE;
                    return super.doInBackground(params);
                }

                @Override
                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);

                    mFinalJsonObj = finalResult;

                    // finalResult.get("")
                    try {
                        onCustomPostExecuteForTokens(result, MESSAGE, MethodName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onCustomPostExecuteForTokens(AppUtils.Async_FAILED_SERVER_UNREACHABLE, MESSAGE, MethodName);
                    }

                }

            };

            mAsyncForAll.execute();
        } else {
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
                else if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPLOAD_FILE))
                    onUploadFileSuccess();
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

    private void onUploadFileSuccess() {
        try {
            imageJsoArray = new JSONArray();
            int id = mFinalJsonObj.getInt("id");
            JSONObject jObj = new JSONObject();
            jObj.put("image" , id);
            imageJsoArray.put(jObj);

            showProgress(true);
            StartAsyncCreate();
        } catch (Exception e) {
            e.printStackTrace();
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
            Snackbar.make(mProgressView, getResources().getString(R.string.message_item_created), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(new Intent(AppUtils.BROADCAST.BR_UPDATE_MY_ITEMS));
                    if (myItemsModelClass != null)
                        setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            }, Snackbar.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("item_name", et_product_name.getText().toString());
            jObj.put("item_description", et_product_desc.getText().toString());
            jObj.put("item_price", ev_price.getText().toString());
            jObj.put("item_price_unit", tv_currency.getText().toString());
            if (isImageChanged)
                jObj.put("images", imageJsoArray);
            return jObj.toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    private List<NameValuePairs> getHeaderNameValuePair() {
        // et_firstname, et_email, et_password, et_confirm_password
        try {
            List<NameValuePairs> list = new ArrayList<NameValuePairs>();
            list.add(new NameValuePairs("x-access-token", AppUtils.getAccessToken(mContext)));
            return list;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }


}
