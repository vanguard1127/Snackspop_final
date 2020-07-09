package com.snackspop.snackspopnew.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONObject;

import java.security.MessageDigest;

public class SplashActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        //getFbKeyHash();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }
        mContext = this;



        String flag_str = null;
        Bundle extras = getIntent().getExtras();
        if( extras != null ) {
            flag_str = extras.getString("chat_flag");
        }

        if (flag_str != null && flag_str.compareTo("true") == 0)
            AppUtils.chatting_flag = true;
        else
            AppUtils.chatting_flag = false;

        if (AppUtils.chatting_flag)
        {
            String user_info = extras.getString("user_info");
            String item_id_string = extras.getString("item_id");
            String message = extras.getString("message");
            try{
                JSONObject obj = new JSONObject(user_info);
                String photoID = obj.getString("photo");
                if (photoID!= null && photoID.compareTo("null") != 0) {
                    AppUtils.chatting_userimageurl = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + photoID;
                }
                else
                    AppUtils.chatting_userimageurl = "";

                AppUtils.chatting_username = obj.getString("first_name") + " " + obj.getString("last_name");
                AppUtils.chatting_userid = obj.getInt("id");
                AppUtils.chatting_itemid = Integer.parseInt(item_id_string);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    boolean first_time = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).
                            getBoolean(AppUtils.PREFS.FIRST_TIME, true);

                    boolean is_logged_in =PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).
                            getBoolean(AppUtils.PREFS.IS_LOGED_IN, false);

                    if (!first_time){
                        if (is_logged_in && AppUtils.chatting_flag) {
                            startActivity(new Intent(mContext, ChattingActivity.class).putExtra(AppUtils.EXTRA.chatting_user_id, AppUtils.chatting_userid)
                                    .putExtra(AppUtils.EXTRA.chatting_user_name, AppUtils.chatting_username).putExtra(AppUtils.EXTRA.chatting_user_image, AppUtils.chatting_userimageurl).putExtra(AppUtils.EXTRA.chatting_item_id, AppUtils.chatting_itemid));
                        }
                        else
                        {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        }
                    }
                    else
                    {
                        AppUtils.setPreferences(AppUtils.PREFS.FIRST_TIME, SplashActivity.this,false);
                        startActivity(new Intent(SplashActivity.this, HelperActivity.class));
                    }


                    finish();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }, 3000);
    }


    private void getFbKeyHash() {
        PackageInfo info;
        try {

            info = getPackageManager().getPackageInfo(
                    "com.snackspop.snackspopnew", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashString = new String(Base64.encode(md.digest(), 0));
                System.out.println("App KeyHash : " + hashString);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}
