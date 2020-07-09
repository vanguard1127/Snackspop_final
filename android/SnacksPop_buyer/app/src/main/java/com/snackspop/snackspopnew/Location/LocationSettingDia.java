package com.snackspop.snackspopnew.Location;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;


/**
 */
public class LocationSettingDia extends Activity {
    TextView txt_cancel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting_dia);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color

            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
         txt_cancel = (TextView) findViewById(R.id.txt_cancel);

        TextView txtDiaTitle = (TextView) findViewById(R.id.txt_diaTitle);
        txtDiaTitle.setText(getString(R.string.app_name));

        TextView txtSetting = (TextView) findViewById(R.id.txt_setting);
        txtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(viewIntent, 10);
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(txt_cancel,"Gps is needed for this feature to work", Snackbar.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AppUtils.isGPSEnabled(this)) {
            LocationSettingDia.this.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {

        Snackbar.make(txt_cancel,"Gps is needed for this feature to work", Snackbar.LENGTH_LONG).show();
        super.onBackPressed();
    }
}
