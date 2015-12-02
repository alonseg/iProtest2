package com.alonseg.iprotest.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.alonseg.iprotest.Consts;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alon on 7/10/2015.
 */
public class Post extends MyBaseObject implements Parcelable{

    private static final String TAG = "POST";

    private String protestName = null;


    private String bodyText = null;
    private String bodySummery = null;

    private long dateAsLong;
    private byte[] postImageArr = null;//image
    private byte[] postThumbArr = null;//image

    public Post(String protestName, String bodyText, Date date, String publisher) {
        super();
        this.protestName = protestName;
        this.bodyText = bodyText;
        this.date = date;
        this.dateAsLong = date != null ? date.getTime() : 0;
        this.publisher = publisher;
        setBodySummery();

    }

    public Post(ParseObject obj){
        this(obj.getString(Consts.P_NAME), obj.getString(Consts.POST_TEXT),
                obj.getCreatedAt(), obj.getString(Consts.POST_CREATED_BY));
        //TODO this hides the images, the FULL SIZE images!
//        if (obj.has(Consts.POST_IMAGE)){
//            ParseFile image = (ParseFile) obj.get(Consts.POST_IMAGE);
//            try {
//                this.postImageArr = image.getData();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        if (obj.has(Consts.POST_THUMB)){
            ParseFile parseThumb= (ParseFile) obj.get(Consts.POST_THUMB);
            try {
                this.postThumbArr = parseThumb.getData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        this.protestID = obj.getString(Consts.P_ID);
        this.itemId = obj.getObjectId();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Bitmap getImage(){
        return this.postImageArr == null ? null : BitmapFactory.decodeByteArray(this.postImageArr,
                0, this.postImageArr.length);
    }

    public String getBodySummery() {
        return bodySummery;
    }

    private void setBodySummery() {
        if (this.bodyText.length() > SUMMERY_LENGTH){
            this.bodySummery = this.bodyText.substring(0,SUMMERY_LENGTH);
            this.bodySummery += "...";
        }else {
            this.bodySummery = this.bodyText;
        }
    }

    public String getBodyText() {
        return bodyText;
    }

    public String getProtestName() {
        return protestName;
    }

    public void setImage(byte[] image){this.postImageArr = image;}

    public void setThumb(byte[] image){this.postThumbArr= image;}

    public byte[] getThumbBytes(){
        return this.postThumbArr;
    }

    public boolean hasImage(){
        return this.postThumbArr != null;
    }

    public Bitmap getThumb(){
        return this.postThumbArr == null ? null : BitmapFactory.decodeByteArray(this.postThumbArr,
                0, this.postThumbArr.length);
    }

    public String getShareMsg(){
        return "Hi there!\nTake a look at \"" + getProtestName() + "\" Protest!:\n\"" +
                getBodySummery() + "\"\nAt http://www.iProtest.com/" + Consts.POST + "/" + getItemId();
    }

    //REVERTS THE LIST ORDER!!!!!
    public static ArrayList<Post> convertListObj(List<Object> retrievedList){
        ArrayList<Post> ret = new ArrayList<>();
        if (retrievedList != null) {
            for (int i = retrievedList.size() - 1; i >=0 ; i--){
                if (((ParseObject) retrievedList.get(i)).has(Consts.P_NAME))
                    ret.add(new Post((ParseObject) retrievedList.get(i)));
            }
        }
        return ret;
    }

    //REVERTS THE LIST ORDER!!!!!
    public static ArrayList<Post> convertListParseObj(List<ParseObject> retrievedPostList){
        ArrayList<Post> ret = new ArrayList<>();
        if (retrievedPostList != null) {
            for (int i = retrievedPostList.size() - 1; i >=0 ; i--){
                ret.add(new Post(retrievedPostList.get(i)));
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
        dest.writeString(protestName);
        dest.writeString(protestID);
        dest.writeString(itemId);
        dest.writeString(bodyText);
        dest.writeString(bodySummery);
        dest.writeString(publisher);

        dest.writeLong(dateAsLong);
        dest.writeByteArray(postImageArr);
        dest.writeByteArray(postThumbArr);
    }

    protected Post(Parcel in) {
        protestName = in.readString();
        protestID = in.readString();
        itemId = in.readString();
        bodyText = in.readString();
        bodySummery = in.readString();
        publisher = in.readString();

        date = new Date(in.readLong());
        postImageArr = in.createByteArray();
        postThumbArr= in.createByteArray();
    }
}
