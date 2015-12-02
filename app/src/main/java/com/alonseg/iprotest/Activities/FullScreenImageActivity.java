package com.alonseg.iprotest.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.alonseg.iprotest.Objects.Log;
import android.widget.ImageView;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FullScreenImageActivity extends Activity {

    private static final String TAG = "FULL_SCRN_IMAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        final ImageView fullScreen = (ImageView) findViewById(R.id.fullScrnImage);
        Intent intent = getIntent();
        if (intent.hasExtra(Consts.POST)) {
            final Post post = intent.getExtras().getParcelable(Consts.POST);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.POST);
            query.getInBackground(post.getItemId(), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        try {
                            byte[] imageArr = ((ParseFile) object.get(Consts.POST_THUMB)).getData();
                            fullScreen.setImageBitmap(BitmapFactory.decodeByteArray(imageArr, 0,
                                    imageArr.length));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Log.v(TAG, "error from getInBack" + e.getMessage());
                    }
                }
            });
        }else
            Log.v(TAG, "there was no post");
    }
}
