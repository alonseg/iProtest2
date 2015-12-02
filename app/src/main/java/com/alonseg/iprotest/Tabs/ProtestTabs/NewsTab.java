package com.alonseg.iprotest.Tabs.ProtestTabs;

/**
 * Created by Alon on 9/4/2015.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Activities.addLocation;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Interfaces.FragmentInterface;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.adapters.PostAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewsTab extends BaseProtestTab implements FragmentInterface {

    public static final String TAG = "NEWS";

    public boolean postingPhoto = false;

    public EditText newPostEditPlaceHolder;

    public Button protestToManage;
    public Button joinThisCause;
    public ListView protestPostsListView;
    public RelativeLayout newPostLayout;

    public ImageButton photoBtn;
    public ImageButton locationBtn;
    public ImageButton doneBtn;

    private TextView noPostsMsg;

    public ImageView newPostImage;

    public PostAdapter postAdapter;
    public ArrayList<Post> postsListArr = new ArrayList<>();
    public Context con;
    public View page;

    public String imagePath = null;
    private Uri imageUri;

    public NewsTab(){
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_news, container, false);
        con = v.getContext();
        page = v;

        initViews(v);

        handlePostList(v);

        handleJoinBtn(joinThisCause, con, TAG);

        handlePhotoBtn();

        handleLocationBtn();

        handleNewPostEdit();

        handleDoneBtn();

        checkAndSwitchIfFollow();

        return v;
    }

    private void handleLocationBtn() {
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, addLocation.class);
//                startActivity(intent);
            }
        });
        locationBtn.setVisibility(View.GONE);
    }

    private void initViews(View v) {
        newPostImage = (ImageView) v.findViewById(R.id.ivImage);
        photoBtn = (ImageButton) v.findViewById(R.id.addPhotosToPost);
        locationBtn = (ImageButton) v.findViewById(R.id.addLocationToPost);
        doneBtn = (ImageButton) v.findViewById(R.id.addNewPostDone);
        newPostEditPlaceHolder = (EditText) v.findViewById(R.id.newPostEditPlaceHolder);
        newPostEditPlaceHolder.setTypeface(Consts.appFont);
        joinThisCause = (Button) v.findViewById(R.id.joinThisCause);
        protestToManage = (Button) v.findViewById(R.id.protestToManage);
        protestPostsListView = (ListView) v.findViewById(R.id.protestPostsList);
        newPostLayout = (RelativeLayout) v.findViewById(R.id.newPostLayout);
        noPostsMsg = (TextView) v.findViewById(R.id.noPostsMsg);
        noPostsMsg.setTypeface(Consts.appFont);

    }

    private void handlePostList(final View v) {
        v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        //get the posts list and display it
        ParseQuery<ParseObject> query = new ParseQuery<>(Consts.PROTEST);
        query.include(Consts.P_POSTS_LIST);
        query.include(Consts.POST_CREATION_TIME);

        query.orderByDescending("createdAt");
        query.getInBackground(ProtestActivity.getId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                List<Object> l = parseObject.getList(Consts.P_POSTS_LIST);
                if (l == null || l.size() < 1){
                    noPostsMsg.setVisibility(View.VISIBLE);
                }
                postsListArr = Post.convertListObj(l);
                postAdapter = new PostAdapter(con, R.layout.row_post, postsListArr);
                protestPostsListView.setAdapter(postAdapter);

                v.findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });
    }



    private void handlePhotoBtn() {
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void handleNewPostEdit() {
        newPostEditPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPostEditPlaceHolder.setLines(5);
                newPostEditPlaceHolder.setVisibility(View.VISIBLE);
                newPostLayout.setVisibility(View.VISIBLE);
                protestPostsListView.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, R.id.newPostLayout);
                protestPostsListView.setLayoutParams(p);
            }
        });
    }

    private void handleDoneBtn() {
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (TextUtils.isEmpty(newPostEditPlaceHolder.getText().toString()) && (!postingPhoto)) {
                    newPostEditPlaceHolder.setError("Try writing a post or uploading a photo!");
                    return;
                }

                page.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                final String text = newPostEditPlaceHolder.getText().toString().trim();
                newPostEditPlaceHolder.setText("");
                newPostEditPlaceHolder.setLines(1);
                newPostLayout.setVisibility(View.GONE);

                ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);
                final ParseObject postParseObj = initNewParsePost(text);

                // Retrieve the object by id
                query.getInBackground(ProtestActivity.getId(), new GetCallback<ParseObject>() {
                    public void done(final ParseObject protestToUpdate, ParseException e) {
                        if (e == null) {

                            protestToUpdate.add(Consts.P_POSTS_LIST, postParseObj);
                            final Post postObj = new Post(postParseObj);

                            //add the photo if there is such
                            if (postingPhoto) {
                                newPostImage.setVisibility(View.GONE);
                                Bitmap bm;
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(imagePath, options);
                                final int REQUIRED_SIZE = 400;
                                int scale = 1;
                                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                                    scale *= 2;
                                options.inSampleSize = scale;
                                options.inJustDecodeBounds = false;
                                bm = BitmapFactory.decodeFile(imagePath, options);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                final byte[] imageAsBytes = stream.toByteArray();

                                final ParseFile imageFile = new ParseFile(Consts.IMAGE_PNG, new byte[10]);
                                imageFile.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Bitmap tempThumb = BitmapFactory.decodeByteArray(imageAsBytes, 0,
                                                    imageAsBytes.length);
                                            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                            tempThumb.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                                            final ParseFile thumbFile = new ParseFile(Consts.IMAGE_THUMB, bytes.toByteArray());
                                            thumbFile.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        postParseObj.put(Consts.POST_THUMB, thumbFile);
//                                                        postParseObj.put(Consts.POST_IMAGE, imageFile);
                                                        protestToUpdate.add(Consts.P_PHOTOS, thumbFile);
                                                        protestToUpdate.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    newPostEditPlaceHolder.setText("");
                                                                    postObj.setThumb(bytes.toByteArray());
                                                                    postAdapter.addItem(postObj);
                                                                    postAdapter.notifyDataSetChanged();
                                                                    noPostsMsg.setVisibility(View.GONE);
                                                                    page.findViewById(R.id.progress).setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        } else
                                            Log.v(TAG, "failed saving image to parse");
                                    }
                                });
                            } else {
                                protestToUpdate.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            postAdapter.addItem(postObj);
                                            postAdapter.notifyDataSetChanged();
                                            page.findViewById(R.id.progress).setVisibility(View.GONE);
                                            noPostsMsg.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(con,"Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                                            page.findViewById(R.id.progress).setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    @NonNull
    private ParseObject initNewParsePost(String text) {
        final ParseObject postParseObj = new ParseObject(Consts.POST);
        postParseObj.put(Consts.POST_TEXT, text);
        postParseObj.put(Consts.P_NAME, ProtestActivity.getName());
        postParseObj.put(Consts.P_ID, ProtestActivity.getId());
        postParseObj.put(Consts.POST_CREATED_BY, app.getUsername());
        postParseObj.put(Consts.LOCAL ,  getUserCountry(con));
        return postParseObj;
    }

    protected void checkAndSwitchIfFollow() {
        if (ProtestActivity.amIFollowing()) {
            newPostEditPlaceHolder.setVisibility(View.VISIBLE);
            joinThisCause.setVisibility(View.GONE);

        } else {
            joinThisCause.setVisibility(View.VISIBLE);
            newPostLayout.setVisibility(View.GONE);
            newPostEditPlaceHolder.setVisibility(View.GONE);
        }
        protestPostsListView.setVisibility(View.VISIBLE);
    }

    private void selectImage() {
        final CharSequence[] items = {Consts.TAKE_PHOTO, Consts.CHOOSE_FROM_GALLERY, "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(Consts.TAKE_PHOTO)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    imageUri = Uri.fromFile(f);
                    imagePath = imageUri.getPath();
                    startActivityForResult(intent, Consts.REQUEST_CAMERA);
                } else if (items[item].equals(Consts.CHOOSE_FROM_GALLERY)) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            Consts.SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            newPostImage.setVisibility(View.VISIBLE);
            if (requestCode == Consts.REQUEST_CAMERA) {
                postingPhoto = true;
                handleImageFromPath(imagePath);
            } else if (requestCode == Consts.SELECT_FILE) {
                postingPhoto = true;
                Uri selectedImageUri = data.getData();

                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);
                imagePath = selectedImagePath;

                handleImageFromPath(selectedImagePath);
            }
        }else {
            Log.v(TAG, "result from photo error " + resultCode);
        }
    }

    private void handleImageFromPath(String selectedImagePath) {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        newPostImage.setImageBitmap(bm);
    }

    @Override
    public void fragmentBecameVisible() {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.tab_news, null);
        handlePostList(v);
        checkAndSwitchIfFollow();
        newPostLayout.setVisibility(View.GONE);

        Log.d(TAG, "switched fragment to news");
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }
}
