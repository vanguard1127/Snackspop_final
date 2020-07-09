package com.snackspop.snackspopnew.Activity.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

public class AboutUsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public AboutUsFragment() {
    }

    public static AboutUsFragment newInstance( ) {
        return  new AboutUsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);

        WebView wv_terms;

        wv_terms = (WebView )rootView.findViewById(R.id.wv_terms);

        wv_terms.getSettings().setLoadsImagesAutomatically(true);
        wv_terms.getSettings().setJavaScriptEnabled(true);
        wv_terms.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //wv_terms.loadUrl("http://www.snackspop.com/aboutus.html");
        wv_terms.loadUrl(AppUtils.BASE_SITE_URL + "/aboutus");
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
