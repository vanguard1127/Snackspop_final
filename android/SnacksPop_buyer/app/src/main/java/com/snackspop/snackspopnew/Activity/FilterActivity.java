package com.snackspop.snackspopnew.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.BubbleThumbRangeSeekbar;
import com.snackspop.snackspopnew.Activity.Fragment.HomeFragment;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;


public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    BubbleThumbRangeSeekbar rangeSeekbar_price;
    SwitchCompat sw_include_closed;
    TextView tv_min, tv_max;
    Button bt_clear, bt_apply;
    int max = 0, min = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        if (getIntent().hasExtra(AppUtils.EXTRA.MAX))
            max = getIntent().getIntExtra(AppUtils.EXTRA.MAX, 0) + 1;
        if (getIntent().hasExtra(AppUtils.EXTRA.MIN))
            min = getIntent().getIntExtra(AppUtils.EXTRA.MIN, 0);

        initUi();
    }

    private void initUi() {
        rangeSeekbar_price = (BubbleThumbRangeSeekbar) findViewById(R.id.rangeSeekbar_price);
        sw_include_closed = (SwitchCompat) findViewById(R.id.sw_include_closed);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_apply = (Button) findViewById(R.id.bt_apply);
        tv_max = (TextView) findViewById(R.id.tv_max);
        tv_min = (TextView) findViewById(R.id.tv_min);

        bt_clear.setOnClickListener(this);
        bt_apply.setOnClickListener(this);

        rangeSeekbar_price.setMaxValue(HomeFragment.maxPriceValue);
        rangeSeekbar_price.setMinValue(HomeFragment.minPriceValue);


        findViewById(R.id.iv_back).setOnClickListener(this);
        // set listener
        rangeSeekbar_price.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tv_min.setText(String.valueOf(minValue));
                tv_max.setText(String.valueOf(maxValue));
            }
        });

        sw_include_closed.setChecked(getIntent().getBooleanExtra(AppUtils.EXTRA.INCLUDE_CLOSED, true));

        rangeSeekbar_price.setMaxStartValue(max);
        rangeSeekbar_price.setMinStartValue(min);
        rangeSeekbar_price.apply();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_clear:
                setResult(Activity.RESULT_CANCELED, new Intent().putExtra(AppUtils.EXTRA.FILTER_IS_CLEARED, true));
                finish();
                break;

            case R.id.bt_apply:

                Intent in = new Intent();
                in.putExtra(AppUtils.EXTRA.MIN_VALUE_SET, Integer.parseInt(tv_min.getText().toString()));
                in.putExtra(AppUtils.EXTRA.MAX_VALUE_SET, Integer.parseInt(tv_max.getText().toString()));
                in.putExtra(AppUtils.EXTRA.INCLUDE_CLOSED, sw_include_closed.isChecked());
                setResult(Activity.RESULT_OK, in);
                finish();

                break;

            case R.id.iv_back:
                setResult(Activity.RESULT_CANCELED, new Intent().putExtra(AppUtils.EXTRA.FILTER_IS_CLEARED, false));
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED, new Intent().putExtra(AppUtils.EXTRA.FILTER_IS_CLEARED, true));
        finish();
    }
}
