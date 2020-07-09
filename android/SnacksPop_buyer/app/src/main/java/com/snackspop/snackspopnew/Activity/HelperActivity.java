package com.snackspop.snackspopnew.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.snackspop.snackspopnew.Activity.Fragment.Helper_First;
import com.snackspop.snackspopnew.Activity.Fragment.Helper_Second;
import com.snackspop.snackspopnew.R;

import me.relex.circleindicator.CircleIndicator;

public class HelperActivity extends FragmentActivity {

    ViewPager pager;
    CircleIndicator pageIndicator;
    TextView txt_skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        txt_skip = (TextView)findViewById(R.id.txt_skip);
        pager = (ViewPager)findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pageIndicator = (CircleIndicator)findViewById(R.id.indicator);
        pageIndicator.setViewPager(pager);

        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelperActivity.this, HomeActivity.class));
                HelperActivity.this.finish();
            }
        });
    }

    private void initUi() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return new Helper_First();
                case 1: return new Helper_Second();
                default: return new Helper_First();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }



}
