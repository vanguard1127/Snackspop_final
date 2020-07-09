package com.snackspop.snackspopnew.FCMUtils;

import android.content.Intent;
import android.text.TextUtils;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.snackspop.snackspopnew.Utils.LogCat;

import org.json.JSONObject;

/**
 * Created by suraj on 22/07/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

//    AsyncForAll mAsyncForAll;
    JSONObject mFinalJsonObj;

    private static final String TAG = "MyFirebaseIIDService";


    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String REGISTRATION_TOKEN = "regToken";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        AppEventsLogger.setPushNotificationsRegistrationId(refreshedToken);

        //Displaying token on logcat
        LogCat.e(TAG, "Refreshed token: " + refreshedToken);

        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        if (!TextUtils.isEmpty(refreshedToken))
            registrationComplete.putExtra(REGISTRATION_TOKEN, refreshedToken);
        sendBroadcast(registrationComplete);
//        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(AppUtils.PREFS.IS_LOGED_IN, false))
//            sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {

//        StartAsyncUpdateToken(token);

    }

//    private void StartAsyncUpdateToken(String token) {
//
//        // Close Asynctask of it is already running
//        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
//            mAsyncForAll.cancel(true);
//            mAsyncForAll = null;
//        }
//
//        // Call Asynctaks now
//        if (AppUtils.haveNetworkConnection(this)) {
//            mAsyncForAll = new AsyncForAll(this, getNameValuePair(token), AppUtils.ASYNC_CLASSNAME.VERIFICATION_CODE,
//                    false, true, false) {
//
//                @Override
//                protected Integer doInBackground(Void... params) {
//                    isPut = true;
//                    urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPDATE;
//                    Integer flag = super.doInBackground(params);
//                    return flag;
//                }
//
//                @Override
//                protected void onPostExecute(Integer result) {
//                    super.onPostExecute(result);
//
//                    mFinalJsonObj = finalResult;
//
//                    // finalResult.get("")
//                    onCustomPostExecuteForTokens(result, MESSAGE, MethodName);
//                }
//            };
//            mAsyncForAll.execute();
//        } else {
//        }
//    }
//
//    public void onCustomPostExecuteForTokens(int result, String Message, String ClassName) {
//        switch (result) {
//            case AppUtils.Async_SUCCESS:
//                onVerifySuccess();
//                break;
//            case AppUtils.Async_FAILED_WITH_DESC:
//                onVerifyFailedDesc(Message);
//                break;
//            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
//                onServerUnreachable();
//                break;
//            default:
//                break;
//        }
//
//    }
//
//    private String getNameValuePair(String token) {
//        try {
//            JSONObject userOBJ = new JSONObject(PreferenceManager.getDefaultSharedPreferences(this)
//                    .getString(AppUtils.PREFS.USER_OBJECT_STRING, ""));
//            String userId = userOBJ.getString(AppUtils.USER_OBJECT.ID);
//            JSONObject jObj = new JSONObject();
//            jObj.put(AppUtils.USER_OBJECT.DEVICE_ID, token);
//            jObj.put(AppUtils.USER_OBJECT.ID, userId);
//
////            list.add(new NameValuePairs("countryId", mPhoneCodeView.getText().toString()));
////            list.add(new NameValuePairs("mobileNumber", mPhoneView.getText().toString()));
//            return jObj.toString();
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//            return null;
//        }
//    }
//
//    protected void onServerUnreachable() {
//
//    }
//
//    protected void onVerifyFailedDesc(String Message) {
//    }
//
//
//    protected void onVerifySuccess() {
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
