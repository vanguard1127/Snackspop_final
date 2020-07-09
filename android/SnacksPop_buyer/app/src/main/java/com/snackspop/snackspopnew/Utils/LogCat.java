package com.snackspop.snackspopnew.Utils;

import android.util.Log;


public class LogCat {


    public static void e(String Message) {
        if (AppUtils.isDebug)
            Log.e("   -->   ", "   " + Message);
    }

    public static void i(String Message) {
        if (AppUtils.isDebug)
            Log.i("   ###   ", "  -->  " + Message);
    }

    public static void e(String MessageTi, String Message) {
        if (AppUtils.isDebug)
            Log.e(MessageTi, "   " + Message);
    }

    public static void i(String MessageTi, String Message) {
        if (AppUtils.isDebug)
            Log.i(MessageTi, "  -->  " + Message);
    }

}
