package com.snackspop.snackspopnew.Activity;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.snackspop.snackspopnew.Utils.LogCat;


/**
 * Created by suraj on 25/11/15.
 */
public class TouchableWrapper extends FrameLayout {

    OnCustomEventListener mListener;

    public interface OnCustomEventListener {
        void onEvent(int EventType);
    }

    public TouchableWrapper(Context context) {
        super(context);
    }
    public void setCustomEventListener(OnCustomEventListener eventListener) {
        mListener = eventListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogCat.e("Map Touched " + event.getAction());
        this.mListener.onEvent(event.getAction());
//        HomeFragment.mMapTouchedState = event.getAction();
        return super.dispatchTouchEvent(event);
    }
}
