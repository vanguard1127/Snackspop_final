package com.snackspop.snackspopnew.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suraj on 2/4/17.
 */

public class MyItemsModelClass implements Parcelable {

    private String name = "";
    private String type = "";
    private String description = "";
    private String id = "";
    private String price = "";


    private String price_unit = "";
    private String imageUrl;
    private boolean isOutOfStock = false;
    private int userId = -1;
    private double distance = -1;
    private String unit ="";

    public String getPrice_unit() {
        return price_unit;
    }

    public void setPrice_unit(String price_unit) {
        this.price_unit = price_unit;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }



    public int getItemsQuantityForCartAndOrder() {
        return itemsQuantityForCartAndOrder;
    }

    public void setItemsQuantityForCartAndOrder(int itemsQuantityForCartAndOrder) {
        this.itemsQuantityForCartAndOrder = itemsQuantityForCartAndOrder;
    }

    private int itemsQuantityForCartAndOrder = 0;

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

    public MyItemsModelClass() {
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
        dest.writeInt(this.userId);
        dest.writeDouble(this.distance);
        dest.writeString(this.unit);
        dest.writeString(this.price_unit);
        dest.writeInt(this.itemsQuantityForCartAndOrder);
    }

    protected MyItemsModelClass(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.id = in.readString();
        this.price = in.readString();
        this.imageUrl = in.readString();
        this.isOutOfStock = in.readByte() != 0;
        this.userId = in.readInt();
        this.distance = in.readDouble();
        this.unit = in.readString();
        this.price_unit = in.readString();
        this.itemsQuantityForCartAndOrder = in.readInt();
    }

    public static final Creator<MyItemsModelClass> CREATOR = new Creator<MyItemsModelClass>() {
        @Override
        public MyItemsModelClass createFromParcel(Parcel source) {
            return new MyItemsModelClass(source);
        }

        @Override
        public MyItemsModelClass[] newArray(int size) {
            return new MyItemsModelClass[size];
        }
    };
}
