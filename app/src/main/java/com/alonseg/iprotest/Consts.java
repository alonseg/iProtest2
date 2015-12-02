package com.alonseg.iprotest;

import android.graphics.Typeface;
import android.widget.TextView;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alon on 10/27/2015.
 */
public class Consts {
    public static final String OBJ_ID = "pObjID";

    public static final String ID = "id";
    public static final String TYPE = "type";

    public static final String PROTEST = "aProtest";
    public static final String P_DESCRIPTION = "pDescInProtest";
    public static final String P_FOLLOWERS_LIST = "pFollowersList";
    public static final String P_POSTS_LIST = "pPostsList";
    public static final String P_STEPS_LIST = "pStepsList";
    public static final String P_PHOTOS = "pPhotos";
    public static final String P_FOLLOWERS_COUNT = "pFollowersInProtest";
    public static final String P_NAME = "pNameInProtest";
    public static final String P_ID = "pID";
    public static final String P_STEP = "pStep";
    public static final String P_ADMIN = "pAdminInProtest";
    public static final String PU_FOLLOWING = "pUFollowingList";
    public static final String POST = "post";
    public static final String POST_ID = "post_id";
    public static final String POST_TEXT = "postText";
    public static final String POST_IMAGE = "postImage";
    public static final String POST_THUMB = "postThumb";
    public static final String POST_CREATED_BY = "postCreator";
    public static final String POST_CREATION_TIME = "createdAt";
    public static final String POST_POST_IMAGE = "post.postImage";
    public static final String DELETE_STR = "Delete";

    public static final String SEARCH_TAGS= "searchTags";

    public static final String USER_ID= "uID";
    public static final String USER_NAME= "uName";

    public static final String REPORT = "report";
    public static final String REPORT_TEXT = "reportTxt";

    public static final String STEP = "step";
    public static final String STEP_MSG = "sMessage";
    public static final String STEP_CREATED_BY = "stepCreator";
    public static final String STEP_PROTEST_ID = "stepProtestId";

    public static final String TO_BE_DELETED= "toDelete";

    public static final String TO_BE_DEL_TYPE= "toBeDelObjType";
    public static final String LOCAL = "local";

    public static String baseLink = "https://play.google.com/store/apps/details?id=com.alonseg.iprotest";

    public static final int REQUEST_CAMERA = 443;
    public static final int SELECT_FILE = 444;
    public static final int DELETE = 12345;



    public static int TYPE_INDEX = 3;
    public static int ID_INDEX = 4;
    public static int USERNAME_TAKEN = 202;

    public static final int LOTS = 30;
    public static final String CHOOSE_FROM_GALLERY = "Choose from Gallery";
    public static final String TAKE_PHOTO = "Take Photo";
    public static final String IMAGE_PNG = "image.PNG";
    public static final String IMAGE_THUMB = "thumbnail.PNG";
    public static final String I_PROTEST_URL = "iProtest";
    public static final int REQ_SIGN = 121;
    public static final int REQ_SIGN_SUC = 125;
    public static final int REQ_SCAN = 201;

    //    public static Typeface appFont;
    public static Typeface dragonFontType;
    public static Typeface appFont;

    public static SimpleDateFormat DATE_FORMAT;

    public static SimpleDateFormat getFormat(){
        return new SimpleDateFormat("dd-MM-yy HH:mm");
    }


    public static void setTextToView(TextView v, String text) {
        v.setText(text);
        v.setTypeface(Consts.appFont);
    }
    
    public static List<ParseObject> getFollowingListObjects(List<String> IDs){
        if (IDs == null || IDs.size() == 0)
            return null;
        List<ParseObject> ret = new ArrayList<>();
        for (String id : IDs) {
            ret.add(ParseObject.createWithoutData(Consts.PROTEST, id));
        }
        return ret;
    }
}
