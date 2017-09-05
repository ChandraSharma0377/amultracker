package com.amul.dc.pojos;

import android.graphics.drawable.Drawable;


public class TransactionBeans {


    private Drawable storeThumb;
    private String storeName;
    private String storeLocation;
    private String scandatetime;
    private boolean isselect = false;
    private String gpscoordinate;
    private byte[] imageOne;
    private byte[] imageTwo;
    private String status;
    private int uniqueId;

    public TransactionBeans(Drawable storeThumb, String storeName, String storeLocation) {
        this.storeThumb = storeThumb;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
    }

    public TransactionBeans(Drawable storeThumb, String storeName, String storeLocation,
                            String scandatetime, boolean isselect, String gpscoordinate,
                            byte[] imageOne, byte[] imageTwo, String status, int uniqueId) {
        this.storeThumb = storeThumb;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.scandatetime = scandatetime;
        this.isselect = isselect;
        this.gpscoordinate = gpscoordinate;
        this.imageOne = imageOne;
        this.imageTwo = imageTwo;
        this.status = status;
        this.uniqueId = uniqueId;
    }

    public TransactionBeans() {

    }

    public Drawable getStoreThumb() {
        return storeThumb;
    }

    public void setStoreThumb(Drawable storeThumb) {
        this.storeThumb = storeThumb;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getScandatetime() {
        return scandatetime;
    }

    public void setScandatetime(String scandatetime) {
        this.scandatetime = scandatetime;
    }

    public boolean isselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public String getGpscoordinate() {
        return gpscoordinate;
    }

    public void setGpscoordinate(String gpscoordinate) {
        this.gpscoordinate = gpscoordinate;
    }

    public byte[] getImageOne() {
        return imageOne;
    }

    public void setImageOne(byte[] imageOne) {
        this.imageOne = imageOne;
    }

    public byte[] getImageTwo() {
        return imageTwo;
    }

    public void setImageTwo(byte[] imageTwo) {
        this.imageTwo = imageTwo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TransactionBeans{");
        sb.append("storeThumb=").append(storeThumb);
        sb.append(", storeName='").append(storeName).append('\'');
        sb.append(", storeLocation='").append(storeLocation).append('\'');
        sb.append(", scandatetime='").append(scandatetime).append('\'');
        sb.append(", isselect=").append(isselect);
        sb.append(", gpscoordinate='").append(gpscoordinate).append('\'');
        sb.append(", imageOne=");
        if (imageOne == null) sb.append("null");
        else {
            sb.append('[');
                sb.append("imageOne");
            sb.append(']');
        }
        sb.append(", imageTwo=");
        if (imageTwo == null) sb.append("null");
        else {
            sb.append('[');
                sb.append("imageTwo");
            sb.append(']');
        }
        sb.append(", status='").append(status).append('\'');
        sb.append(", uniqueId=").append(uniqueId);
        sb.append('}');
        return sb.toString();
    }
}

