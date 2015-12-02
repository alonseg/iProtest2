package com.alonseg.iprotest.Objects;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alon on 7/10/2015.
 */
public class Protest extends MyBaseObject implements Parcelable{

    private final int SUMMERY_LENGTH = 170; // +3 for ...
    public static final String TAG = "PROTEST";
    public static final String postID = "objectId";

    private String name = null;
    private String description = null;
    private String descriptionSummary = null;
    private String admin;
    private int count;
    private JSONArray followers;
    private Drawable img = null;//image
    private ArrayList<Post> postsList = null;


    private List<String> postsIds = new ArrayList<>();
    private boolean amIFollowing;
    boolean selected = false;



    public Protest(String name, String bodyText, String itemId) {
        super();
        this.name = name;
        this.itemId = itemId;
        this.description = bodyText;
        if (bodyText.length() > SUMMERY_LENGTH){
            this.descriptionSummary = bodyText.substring(0,SUMMERY_LENGTH);
        }else {
            this.descriptionSummary = bodyText;
        }
        this.descriptionSummary += "...";
    }

    public Protest(ParseObject obj) {
        this(obj.get(Consts.P_NAME).toString().trim(),
                obj.get(Consts.P_DESCRIPTION).toString(), obj.getObjectId());
        followers = obj.getJSONArray(Consts.P_FOLLOWERS_LIST);
        String username = "\"" + app.getUsername() + "\"";
        amIFollowing = (followers != null && followers.toString().contains(username));
        admin = obj.getString(Consts.P_ADMIN);
        count = obj.getInt(Consts.P_FOLLOWERS_COUNT);

        JSONArray posts = obj.getJSONArray(Consts.P_POSTS_LIST);
        if (posts != null){
            for (int i = 0 ; i < posts.length() ; i++){
                JSONObject jO;
                try {
                    jO = posts.getJSONObject(i);
                    postsIds.add(jO.getString(postID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //############################### GETTERS SETTERS ######################################
    public String getDescriptionSummary() {
        return descriptionSummary;
    }

    private void setBodySummery() {
        if (this.description.length() > SUMMERY_LENGTH){
            this.descriptionSummary = this.description.substring(0,SUMMERY_LENGTH);
        }else {
            this.descriptionSummary = this.description;
        }
        this.descriptionSummary += "...";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.setBodySummery();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage(){ return this.img; }
    public void setImage(Drawable image){this.img = image;}

    public boolean amIFollowing() {
        return app.logged() && amIFollowing;
    }

    public String getAdmin() {
        return admin;
    }

    public int getCount() {
        return count;
    }

    public String getShareMsg(){
        return "Hi there!\nTake a look at \"" + getName() + "\" Protest!:\n\"" +
                getDescriptionSummary() + "\"\nAt http://www.iProtest.com/" + Consts.PROTEST +
                "/" +getItemId();
    }

    public static ArrayList<Protest> convertList(List<ParseObject> retrievedList){
        ArrayList<Protest> ret = new ArrayList<>();
        for (ParseObject obj : retrievedList){
            ret.add(new Protest(obj));
        }
        return ret;
    }

    public static ArrayList<byte[]> convertPhotoList(List<Object> retrievedList){
        ArrayList<byte[]> ret = new ArrayList<>();
        if (retrievedList == null)
            return ret;

        for (Object obj : retrievedList){
            ParseFile image = (ParseFile) obj;
            try {
                ret.add(image.getData());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(descriptionSummary);
        dest.writeString(itemId);
        dest.writeString(admin);
        dest.writeInt(count);

        dest.writeByte((byte) (amIFollowing ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    protected Protest(Parcel in) {
        name = in.readString();
        description = in.readString();
        descriptionSummary = in.readString();
        itemId = in.readString();
        admin = in.readString();
        count = in.readInt();

        amIFollowing = in.readByte() != 0;
        selected = in.readByte() != 0;
    }

    public static final Creator<Protest> CREATOR = new Creator<Protest>() {
        @Override
        public Protest createFromParcel(Parcel in) {
            return new Protest(in);
        }

        @Override
        public Protest[] newArray(int size) {
            return new Protest[size];
        }
    };
}
