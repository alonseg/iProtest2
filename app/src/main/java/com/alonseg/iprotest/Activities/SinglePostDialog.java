package com.alonseg.iprotest.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import com.alonseg.iprotest.Objects.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.MyBaseObject;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Alon on 7/15/2015.
 */
public class SinglePostDialog extends Activity{

    public static final String TAG = "SINGLE_POST";

    public TextView ttl;
    public TextView body;
    public ImageView img;
    public ImageButton shareWhatsapp;
    public ImageButton shareAll;
    public Post post;

    public Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.single_post);
        con = this;


        Intent intent = getIntent();
        if (intent.hasExtra(Consts.POST)){
            post = intent.getExtras().getParcelable(Consts.POST);
        }else {
            Log.d(TAG, "started post without a post obj");
            finish();
        }

        initViews();

        handleTitle();

        Consts.setTextToView(body, post.getBodyText());

        handleImage();

        handleWhatsApp();

        handleShare();

    }

    private void handleTitle() {
        Consts.setTextToView(ttl, post.getProtestName());
        ttl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, ProtestActivity.class);
                intent.putExtra(Consts.P_ID, post.getProtestID());
                con.startActivity(intent);
            }
        });
    }

    private void handleShare() {
        shareAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = getIntentToSharePost(post);
                startActivity(Intent.createChooser(share, "Share link!"));
            }
        });
    }

    private void handleWhatsApp() {
        shareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = getIntentToSharePost(post);
                share.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(share, "Share link!"));
            }
        });
    }

    private void handleImage() {
        img.setImageBitmap(post.getThumb());
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!post.hasImage())
                    return;
                Intent intent = new Intent(con, FullScreenImageActivity.class);
                intent.putExtra(Consts.POST, post);
//                startActivity(intent);
            }
        });
    }

    @NonNull
    public static Intent getIntentToSharePost(MyBaseObject obj) {
        Intent share = new Intent(Intent.ACTION_SEND);

        String msg = obj.getShareMsg();

        byte[] imageAsBytes = obj.getThumbBytes();

        if (imageAsBytes != null) {
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(imageAsBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        }
        String type = imageAsBytes == null ? "text/plain" : "*/*";

        share.setType(type);
        share.putExtra(Intent.EXTRA_TEXT, msg);
        return share;
    }

    private void initViews() {
        ttl = (TextView) findViewById(R.id.singlePostTitle);
        body = (TextView) findViewById(R.id.singlePostBody);
        img = (ImageView) findViewById(R.id.singlePostImage);
        shareWhatsapp = (ImageButton) findViewById(R.id.btnWhatsappPost);
        shareAll = (ImageButton) findViewById(R.id.btnSharePost);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
