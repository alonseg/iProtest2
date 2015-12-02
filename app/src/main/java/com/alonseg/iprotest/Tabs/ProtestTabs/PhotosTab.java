package com.alonseg.iprotest.Tabs.ProtestTabs;

/**
 * Created by Alon on 9/4/2015.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Interfaces.FragmentInterface;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.adapters.PhotosAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class PhotosTab extends BaseProtestTab implements FragmentInterface{

    public static final String TAG = "PHOTOS";
    public static Button joinThisCause;
    public static GridView photoGrid;

    public Context con;
    public ArrayList<byte[]> photosArr;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_photos,container,false);
        con = v.getContext();

        joinThisCause = (Button) v.findViewById(R.id.joinThisCause);
        photoGrid = (GridView) v.findViewById(R.id.photosGrid);

        handleJoinBtn(joinThisCause, con, TAG);

        ParseQuery<ParseObject> query = new ParseQuery<>(Consts.PROTEST);
        query.include(Consts.P_PHOTOS);
        query.getInBackground(ProtestActivity.getId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                photosArr = Protest.convertPhotoList(parseObject.getList(Consts.P_PHOTOS));

                final PhotosAdapter photosAdapter = new PhotosAdapter(con,R.layout.simple_image, parseObject.getList(Consts.P_PHOTOS));
                photoGrid.setAdapter(photosAdapter);
            }
        });
        return v;
    }

    protected void checkAndSwitchIfFollow() {
        if (ProtestActivity.amIFollowing()){
            joinThisCause.setVisibility(View.GONE);
        }else {
            joinThisCause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void fragmentBecameVisible() {
        Log.v(TAG, "switched fragment to photos");
        checkAndSwitchIfFollow();
    }
}