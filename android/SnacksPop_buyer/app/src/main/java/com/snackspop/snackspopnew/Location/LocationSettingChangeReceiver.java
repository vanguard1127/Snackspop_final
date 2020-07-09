package com.snackspop.snackspopnew.Location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.snackspop.snackspopnew.Utils.AppUtils;


public class LocationSettingChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
          /*  Toast.makeText(context, "in android.location.PROVIDERS_CHANGED" + Util.isGpsOn(context),
                    Toast.LENGTH_SHORT).show();*/
            if (!AppUtils.isGPSEnabled(context)) {
                Intent dia = new Intent(context, LocationSettingDia.class);
                dia.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dia);
            }
        }
    }
}