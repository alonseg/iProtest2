package com.alonseg.iprotest.Objects;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Consts;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alon on 11/8/2015.
 */
public class Step extends MyBaseObject {

    private Date date;
    private String publisher;
    private String msg;

    public Step(String msg, Date date, String publisher){
        this.msg = msg;
        this.date = date;
        this.publisher = publisher;
    }

    public Step(ParseObject parseObject) {
        this(parseObject.getString(Consts.STEP_MSG), parseObject.getCreatedAt(),
                parseObject.getString(Consts.STEP_CREATED_BY));
        protestID = parseObject.getString(Consts.STEP_PROTEST_ID);
        itemId = parseObject.getObjectId();

    }

    public String getMsg() {
        return msg;
    }

    public Date getDate() {
        return date;
    }

    public String getPublisher(){
        return publisher;
    }

    public String getShareMsg(){
        return "Hi there!\nTake a look at \"" + ProtestActivity.getName() + "\" Protest!:\n\"" +
                getMsg() + "\"\nAt http://www.iProtest.com/" + Consts.STEP + "/" + getItemId();
    }

    public static ArrayList<Step> convertListObj(List<Object> list) {
        ArrayList<Step> ret = new ArrayList<>();
        if (list != null) {
            for (int i = list.size() - 1; i >=0 ; i--){
                ret.add(new Step((ParseObject) list.get(i)));
            }
        }
        return ret;
    }
}
