package com.snackspop.snackspopnew.Activity.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.snackspop.snackspopnew.Activity.TouchableWrapper;

/**
 * Created by suraj on 25/11/15.
 */
public class MySupportMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    public TouchableWrapper mTouchView;


    OnCustomEventListener mListener;

    public interface OnCustomEventListener {
        void onEvent(int EventType);
    }

    public void setCustomEventListener(OnCustomEventListener eventListener) {
        mListener = eventListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        mTouchView.setCustomEventListener(new TouchableWrapper.OnCustomEventListener() {
            @Override
            public void onEvent(int EventType) {
                if( mListener != null )
                    mListener.onEvent(EventType);
            }
        });
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }
}