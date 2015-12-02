package com.alonseg.iprotest.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import com.alonseg.iprotest.Objects.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.alonseg.iprotest.Activities.SinglePostDialog;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.MyBaseObject;
import com.alonseg.iprotest.Objects.ViewHolder;
import com.alonseg.iprotest.R;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Alon on 11/1/2015.
 */
public class MyBaseAdapter extends ArrayAdapter<MyBaseObject>{
    public static final String TAG = "MY_BASE";

    public ArrayList<MyBaseObject> myBaseObjs;

    public static final int GROUP_ID = 0;
    public static final int ORDER = 3;
    public String type = null;
    public String whichList= null;

    public MyBaseAdapter(Context context, int resource, String type, String whichList) {
        super(context, resource);
        this.type = type;
        this.whichList = whichList;
    }

    public void handleOptionsBtn(ViewHolder holder, final Context con, final boolean canDel,
                                 final int pos) {
        holder.optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(con, v);

                popup.getMenuInflater().inflate(R.menu.popup_post, popup.getMenu());
                if (canDel) {
                    popup.getMenu().add(GROUP_ID, Consts.DELETE, ORDER, Consts.DELETE_STR);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sharePost:
                                handleShare(myBaseObjs.get(pos), con);
                                break;
                            case R.id.reportPost:
                                handleReport(myBaseObjs.get(pos).getItemId(), con);
                                break;
                            case Consts.DELETE:
                                handleDelete(myBaseObjs.get(pos).getItemId(),
                                        myBaseObjs.get(pos).getProtestID(),pos );

                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    private void handleShare(MyBaseObject obj, Context con) {
        Intent share = SinglePostDialog.getIntentToSharePost(obj);
        con.startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void handleDelete(final String itemID, final String protestID, final int pos) {

        new AlertDialog.Builder(getContext()).setTitle("Delete item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        ParseQuery<ParseObject> query = ParseQuery.getQuery(type);
                        query.getInBackground(itemID, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject toBeDeleted, ParseException e) {
                                if (toBeDeleted == null)
                                    return;
                                if (protestID != null) {
                                    proceedToDelete(toBeDeleted, protestID);
                                    myBaseObjs.remove(pos);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void proceedToDelete(final ParseObject toBeDeleted, String protestID) {
        final ArrayList<ParseObject> removeArr = new ArrayList<>();
        removeArr.add(toBeDeleted);

        ParseFile imageFile = (ParseFile) toBeDeleted.get(Consts.POST_THUMB);
        final ArrayList<ParseFile> removeArr2 = new ArrayList<>();
        if (imageFile != null) {
            removeArr2.add(imageFile);
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);
        query.include(whichList);
        query.getInBackground(protestID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.removeAll(whichList, removeArr);

                    List<Object> oldArr = object.getList(whichList);
                    oldArr.removeAll(removeArr);
                    object.put(whichList, oldArr);
                    object.removeAll(Consts.P_PHOTOS, removeArr2);
                    saveAndDelete(object, toBeDeleted);
                } else {
                    Log.d(TAG, "query failed " + e.getMessage());
                }
            }
        });
    }

    private void saveAndDelete(ParseObject object, final ParseObject toBeDeleted) {
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.v(TAG, "saved the protest after removing item");
                    toBeDeleted.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.v(TAG, "deleted successfully");
                            }
                        }
                    });
                }else{
                    Log.v(TAG, "failed to remove protest");
                }
            }
        });
    }

    private static void handleReport(final String reportedID, final Context con) {
        LayoutInflater li = LayoutInflater.from(con);
        View promptsView = li.inflate(R.layout.report_prompt, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
        TextView reportTtlView = (TextView) promptsView.findViewById(R.id.reportTtl);
        reportTtlView.setTypeface(Consts.appFont);
        // set prompts.xml to alert dialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.reportText);
        ImageButton okBtn = (ImageButton) promptsView.findViewById(R.id.reportOkBtn);
        ImageButton cnclBtn = (ImageButton) promptsView.findViewById(R.id.reportCnclBtn);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userInput.getText().toString())) {
                    ParseObject report = new ParseObject(Consts.REPORT);
                    report.put(Consts.REPORT_TEXT, userInput.getText().toString());
                    String name = app.logged() ? app.getUsername() : "-notLogged";
                    report.put(Consts.USER_NAME, name );
                    report.put(Consts.OBJ_ID, reportedID);
                    report.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(con, "Thanks for reporting!", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                            }
                            alertDialog.dismiss();
                        }
                    });
                }else {
                    userInput.setError("can\'t send an empty report [:~(]");
                }
            }
        });

        cnclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    @Override
    public int getCount() {
        return this.myBaseObjs.size();
    }

    public void addItem(MyBaseObject myBaseObj){
        if (myBaseObj != null)
            myBaseObjs.add(0, myBaseObj);
    }

}
