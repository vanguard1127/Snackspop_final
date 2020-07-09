package com.snackspop.snackspopnew.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by suraj on 2/4/17.
 */

public class MyChatItemModel implements Parcelable {

    private int user_id;
    private Date   date;
    private int direction;
    private String message;
    private String image1Url;
    private String image2Url;

    private JSONObject object;

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.user_id);
        dest.writeString(this.date.toString());
        dest.writeInt(this.direction);
        dest.writeString(this.message);
        dest.writeString(this.image1Url);
        dest.writeString(this.image2Url);
    }

    public String getImage1Url() {
        return image1Url;
    }

    public void setImage1Url(String image1Url) {
        this.image1Url = image1Url;
    }

    public String getImage2Url() {
        return image2Url;
    }

    public void setImage2Url(String image2Url) {
        this.image2Url = image2Url;
    }

    public MyChatItemModel()
    {

    }
    protected MyChatItemModel(Parcel in) {
        this.user_id = in.readInt();
        try{
            String str = in.readString();
            this.date = new SimpleDateFormat("dd/MM/yyyy").parse(str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.direction = in.readInt();
        this.message = in.readString();
        this.image1Url = in.readString();
        this.image2Url = in.readString();
    }

    public static final Creator<MyChatItemModel> CREATOR = new Creator<MyChatItemModel>() {
        @Override
        public MyChatItemModel createFromParcel(Parcel source) {
            return new MyChatItemModel(source);
        }

        @Override
        public MyChatItemModel[] newArray(int size) {
            return new MyChatItemModel[size];
        }
    };
}
