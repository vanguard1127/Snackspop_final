package com.snackspop.snackspopnew.Model;

import android.graphics.Bitmap;

/**
 * Created by suraj on 14/9/15.
 */
public class NameValuePairs {

    public NameValuePairs() {
    }

    public NameValuePairs(String name, String Value) {
        this.name = name;
        this.value = Value;

    }

    public NameValuePairs(String name, Bitmap bmp, String value) {
        this.name = name;
        this.bmp = bmp;
        this.value= value;
        this.isFile = true;

    }

    public NameValuePairs(String name, String Value, boolean isFile) {
        this.name = name;
        this.value = Value;
        this.isFile = isFile;

    }

    private String name, value;
    private Bitmap bmp;
    boolean isFile = false;

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
