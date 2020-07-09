package com.snackspop.snackspopnew.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by suraj on 2/4/17.
 */

public class ChatItemsModelClass implements Parcelable {

    private String name = "";
    private String type = "";
    private String description = "";
    private String id = "";
    private String price = "";
    private String imageUrl;
    private boolean isOutOfStock = false;
    private int userid = -1;

    private double distance = -1;
    private String unit ="";
    private int chat_count;
    private int chat_unread_count;
    private String message;

    private int from_user_id = -1;
    private int to_user_id = -1;

    private String username;
    private String user_image;

    private Date createdDate;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(int from_user_id) {
        this.from_user_id = from_user_id;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(int to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getChat_count() {
        return chat_count;
    }

    public void setChat_count(int chat_count) {
        this.chat_count = chat_count;
    }

    public int getChat_unread_count() {
        return chat_unread_count;
    }

    public void setChat_unread_count(int chat_unread_count) {
        this.chat_unread_count = chat_unread_count;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }




    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ChatItemsModelClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOutOfStock() {
        return isOutOfStock;
    }

    public void setOutOfStock(boolean outOfStock) {
        isOutOfStock = outOfStock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeString(this.id);
        dest.writeString(this.price);
        dest.writeString(this.imageUrl);
        dest.writeByte(this.isOutOfStock ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.distance);
        dest.writeString(this.unit);
        dest.writeInt(this.userid);
        dest.writeInt(this.from_user_id);
        dest.writeInt(this.to_user_id);
        dest.writeString(this.username);
        dest.writeString(this.user_image);

    }

    protected ChatItemsModelClass(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.id = in.readString();
        this.price = in.readString();
        this.imageUrl = in.readString();
        this.isOutOfStock = in.readByte() != 0;
        this.distance = in.readDouble();
        this.unit = in.readString();
        this.userid = in.readInt();
        this.from_user_id = in.readInt();
        this.to_user_id = in.readInt();
        this.username = in.readString();
        this.user_image = in.readString();

    }

    public static final Creator<ChatItemsModelClass> CREATOR = new Creator<ChatItemsModelClass>() {
        @Override
        public ChatItemsModelClass createFromParcel(Parcel source) {
            return new ChatItemsModelClass(source);
        }

        @Override
        public ChatItemsModelClass[] newArray(int size) {
            return new ChatItemsModelClass[size];
        }
    };
}
