package com.alonseg.iprotest.adapters;

import android.content.Context;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.MyBaseObject;

/**
 * Created by Alon on 11/1/2015.
 */
public class BasePostAdapter extends MyBaseAdapter{
    public static final String TAG = "HELPERS";

    public BasePostAdapter(Context context, int resource) {
        super(context, resource, Consts.POST, Consts.P_POSTS_LIST);
    }

    public MyBaseObject getItem(int position){
        if (position < this.myBaseObjs.size()){
            return this.myBaseObjs.get(position);
        }
        return null;
    }
}
