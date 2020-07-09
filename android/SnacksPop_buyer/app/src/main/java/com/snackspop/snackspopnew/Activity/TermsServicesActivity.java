package com.snackspop.snackspopnew.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.snackspop.snackspopnew.R;

public class TermsServicesActivity extends AppCompatActivity {

    View iv_back;
    WebView wv_terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_services);
        initUi();
    }

    private void initUi() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wv_terms = (WebView )findViewById(R.id.wv_terms);

        wv_terms.getSettings().setLoadsImagesAutomatically(true);
        wv_terms.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv_terms.loadUrl("http://www.snackspop.com/seller.html");
    }
}
