package com.snackspop.snackspopnew.Async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.snackspop.snackspopnew.Model.NameValuePairs;
import com.snackspop.snackspopnew.Utils.AppUtils;
import com.snackspop.snackspopnew.Utils.LogCat;

import org.json.JSONObject;

import java.util.List;


public class AsyncForAll extends AsyncTask<Void, Void, Integer> {
    private String TAG = getClass().getName();
    protected String MESSAGE = "";
    private Context mContext;
    SharedPreferences pref;
    protected Editor editor;
    ProgressDialog pd;
    List<NameValuePairs> nameValuePairs;
    String nameValuePairsJsonString;
    protected JSONObject finalResult;
    protected String MethodName;
    protected String urlString;
    protected String accessToken="";
    protected boolean isPut = false;
    protected boolean isDelete = false;
    protected boolean isPost = true;
    protected boolean isMultiPart = false;
    private boolean isPDShow = true;
    List<NameValuePairs> headernameValuePairs;
    String response;

    public void setAccessToken(String token)
    {
        accessToken = token;
    }
    public AsyncForAll(Context mcontext, List<NameValuePairs> nameValuePairs,
                       String MethodName, List<NameValuePairs> headernameValuePairs) {
        // TODO Auto-generated constructor stub
        this.mContext = mcontext;
        this.nameValuePairs = nameValuePairs;
        this.MethodName = MethodName;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.headernameValuePairs = headernameValuePairs;
    }

    public AsyncForAll(Context mcontext, String MethodName,
                       List<NameValuePairs> headernameValuePairs,
                       boolean isPDShow) {
        // TODO Auto-generated constructor stub
        this.mContext = mcontext;
        this.MethodName = MethodName;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.isPDShow = isPDShow;
        this.headernameValuePairs = headernameValuePairs;
    }

    public AsyncForAll(Context mcontext, List<NameValuePairs> nameValuePairs,
                       String MethodName, List<NameValuePairs> headernameValuePairs,
                       boolean isPDShow) {
        // TODO Auto-generated constructor stub
        this.mContext = mcontext;
        this.nameValuePairs = nameValuePairs;
        this.MethodName = MethodName;
        this.isPDShow = isPDShow;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.headernameValuePairs = headernameValuePairs;
    }

    public AsyncForAll(Context mcontext, List<NameValuePairs> nameValuePairs,
                       String MethodName, List<NameValuePairs> headernameValuePairs,
                       boolean isPDShow, boolean isPost) {
        // TODO Auto-generated constructor stub
        this.mContext = mcontext;
        this.nameValuePairs = nameValuePairs;
        this.MethodName = MethodName;
        this.isPDShow = isPDShow;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.headernameValuePairs = headernameValuePairs;
        this.isPost = isPost;
    }

    public AsyncForAll(Context mcontext, String nameValuePairs,
                       String MethodName,
                       boolean isPDShow, boolean isPost) {
        // TODO Auto-generated constructor stub
        this.mContext = mcontext;
        this.nameValuePairsJsonString = nameValuePairs;
        this.MethodName = MethodName;
        this.isPDShow = isPDShow;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.isPost = isPost;
    }


    public AsyncForAll(Context mcontext, List<NameValuePairs> nameValuePairs,
                       String MethodName, List<NameValuePairs> headernameValuePairs,
                       boolean isPDShow, boolean isPost, boolean isMultiPart) {
        // TODO Auto-generated constructor stub
        this.mContext = mcontext;
        this.nameValuePairs = nameValuePairs;
        this.MethodName = MethodName;
        this.isPDShow = isPDShow;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.headernameValuePairs = headernameValuePairs;
        this.isPost = isPost;
        this.isMultiPart = isMultiPart;
    }


    public AsyncForAll(Context mcontext, String nameValuePairsString,
                       String MethodName,
                       boolean isPDShow, boolean isPost, boolean isMultiPart) {
        // TODO Auto-generated constructor stub
        this.mContext = mcontext;
        this.nameValuePairsJsonString = nameValuePairsString;
        this.MethodName = MethodName;
        this.isPDShow = isPDShow;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.isPost = isPost;
        this.isMultiPart = isMultiPart;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        if (pd == null && isPDShow) {
            pd = new ProgressDialog(mContext);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(true);
            pd.setMessage(AppUtils.PROGRESS_DEFAULT_MESSAGE);
            pd.show();
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        // TODO Auto-generated method stub
        int ret;

        LogCat.i("Assync URL", "URL: " + urlString);
        if (isPost) {
            if (!isMultiPart) {
                if (isPut)
                    response = AppUtils.putWithOutMultipartWithAccessToken(urlString,
                            nameValuePairsJsonString,accessToken);
                else if (isDelete)
                    response = AppUtils.deleteWithOutMultipartWithAccessToken(urlString,accessToken);
                else
                    response = AppUtils.postWithOutMultipartWithAccessToken(urlString,
                            nameValuePairsJsonString,accessToken);
            } else {
                response = AppUtils.postWithHeaderNew(urlString,
                        nameValuePairs, headernameValuePairs);
            }
        } else
            response = AppUtils.getWithHeader(urlString);

        if (response != null) {

            LogCat.e("Assync Response", "Response : --> " + response);
            try {
                finalResult = new JSONObject(response);
                String SUCCESS;
                SUCCESS = finalResult.getString("success");
                if (SUCCESS.equals("true")) {
                    ret = AppUtils.Async_SUCCESS;
                    // StolUtils.getUsersDetails(
                    // finalResult.getJSONObject("DATA"), editor);
                    if (finalResult.has("message"))
                        MESSAGE = finalResult.getString("message");

                } else {
                    Log.i(TAG, "Success Failed");
                    MESSAGE = AppUtils.getJSONStringValue(finalResult,"message");
                    if (TextUtils.isEmpty(MESSAGE))
                        MESSAGE = AppUtils.getJSONStringValue(finalResult,"error");
                    ret = AppUtils.Async_FAILED_WITH_DESC;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ret = AppUtils.Async_FAILED_SERVER_UNREACHABLE;
            }
        } else {
            ret = AppUtils.Async_FAILED_SERVER_UNREACHABLE;
            Log.i(TAG, "Response null");
        }
        Log.i(TAG, (ret) + "");
        return Integer.valueOf(ret);
    }

    @Override
    protected void onPostExecute(Integer result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (pd != null && pd.isShowing()) {
            pd.cancel();
            pd = null;
        }
    }
}
