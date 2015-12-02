package com.alonseg.iprotest.Objects;

import java.util.Date;

/**
 * Created by Alon on 11/8/2015.
 */
public class MyBaseObject implements Comparable {
    static final int SUMMERY_LENGTH = 170; // +3 for ...
    protected String itemId = null;
    protected String publisher = null;
    protected String protestID = null;
    protected Date date= new Date();


    public String getProtestID() {
        return protestID;
    }

    public String getPublisher() {
        return publisher != null ? publisher : "Unknown";
    }

    public Date getPublishDate() {
        return date == null ? new Date() : date;
    }

    public String getItemId(){
        return itemId;
    }

    @Override
    public int compareTo(Object another) {
        MyBaseObject myObj = (MyBaseObject) another;
        return date.before(myObj.getPublishDate()) ? 1 : -1;
    }

    public boolean hasImage(){
        return false;
    }

    public byte[] getThumbBytes(){
        return null;
    }

    public String getShareMsg(){
        return "";
    }
}
