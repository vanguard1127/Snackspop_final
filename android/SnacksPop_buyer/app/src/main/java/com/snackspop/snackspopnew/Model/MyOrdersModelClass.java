package com.snackspop.snackspopnew.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by suraj on 5/4/17.
 */

public class MyOrdersModelClass implements Parcelable {

    public void setId(String id) {
        this.id = id;
    }

    private String id = "";
    private String customerName = "";
    private String customerEmail = "";
    private String customerPhone = "";
    private String orderNumber = "";
    private String orderDate = "";
    private String orderTime = "";
    private String orderAddress = "";
    private String status = "";
    private String orderPositionAddress = "";


    private int totalItem;
    private double totalAmount;

    private double deliveryCharges;

    private ArrayList<MyItemsModelClass> itemsList;

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }


    public String getId() {
        return id;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public ArrayList<MyItemsModelClass> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<MyItemsModelClass> itemsList) {
        this.itemsList = itemsList;
    }

    public MyOrdersModelClass() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderPositionAddress(){
        return orderPositionAddress;
    }
    public void setOrderPositionAddress(String orderPositionAddress){
        this.orderPositionAddress = orderPositionAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.customerName);
        dest.writeString(this.customerEmail);
        dest.writeString(this.customerPhone);
        dest.writeString(this.orderNumber);
        dest.writeString(this.orderDate);
        dest.writeString(this.orderTime);
        dest.writeString(this.orderAddress);
        dest.writeString(this.status);
        dest.writeInt(this.totalItem);
        dest.writeDouble(this.totalAmount);
        dest.writeDouble(this.deliveryCharges);
        dest.writeTypedList(this.itemsList);
        dest.writeString(this.orderPositionAddress);
    }

    protected MyOrdersModelClass(Parcel in) {
        this.id = in.readString();
        this.customerName = in.readString();
        this.customerEmail = in.readString();
        this.customerPhone = in.readString();
        this.orderNumber = in.readString();
        this.orderDate = in.readString();
        this.orderTime = in.readString();
        this.orderAddress = in.readString();
        this.status = in.readString();
        this.totalItem = in.readInt();
        this.totalAmount = in.readDouble();
        this.deliveryCharges = in.readDouble();
        this.itemsList = in.createTypedArrayList(MyItemsModelClass.CREATOR);
        this.orderPositionAddress = in.readString();
    }

    public static final Creator<MyOrdersModelClass> CREATOR = new Creator<MyOrdersModelClass>() {
        @Override
        public MyOrdersModelClass createFromParcel(Parcel source) {
            return new MyOrdersModelClass(source);
        }

        @Override
        public MyOrdersModelClass[] newArray(int size) {
            return new MyOrdersModelClass[size];
        }
    };
}
