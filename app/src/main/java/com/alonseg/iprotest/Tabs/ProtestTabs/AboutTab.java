package com.alonseg.iprotest.Tabs.ProtestTabs;

/**
 * Created by Alon on 9/4/2015.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.alonseg.iprotest.Objects.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alonseg.iprotest.Activities.ManageActivity;
import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Interfaces.FragmentInterface;
import com.alonseg.iprotest.R;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class AboutTab extends BaseProtestTab implements FragmentInterface{

    public static final String TAG = "ABOUT";
    public static TextView aboutText;
    public static Button joinThisCause;
    public static Button unfollowBtn;
    public static Button deleteProtestView;

    public Context con;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_about,container,false);
        con = v.getContext();

        initViews(v);

        handleJoinBtn(joinThisCause, con, TAG);

        if (ProtestActivity.isAdmin()){
            deleteProtestView.setVisibility(View.VISIBLE);
        }

        handleDelete();

        handleUnfollow();

        aboutText.setText(ProtestActivity.getDescription());
        return v;
    }

    private void handleUnfollow() {
        if (ProtestActivity.amIFollowing()){
            unfollowBtn.setVisibility(View.VISIBLE);
        }

        unfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(con);
                alertDialog.setTitle("Unfollow Protest");
                alertDialog.setMessage("Are you sure? [:0]");
                alertDialog.setPositiveButton("Unfollow",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //unfollow from user side
                                app.removeAll(Consts.PU_FOLLOWING,
                                        new ArrayList<String>() {{
                                            add(ProtestActivity.getId());
                                        }});

                                //unfollow from Protest side
                                ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);
                                query.include(Consts.P_FOLLOWERS_LIST);
                                query.getInBackground(ProtestActivity.getId(), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(final ParseObject object, ParseException e) {
                                        if (e == null) {
                                            object.removeAll(Consts.P_FOLLOWERS_LIST, new ArrayList<String>() {{
                                                add(app.getUsername());
                                            }});
                                            object.increment(Consts.P_FOLLOWERS_COUNT, -1);
                                            object.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        restartProtest(TAG, object);
                                                        unsubscribePush();
                                                        Log.d(TAG, "saved protest after unfollowing");
                                                    } else {
                                                        Log.d(TAG, "failed to save protest after unfollowing");
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "failed to get protest for unfollowing");
                                        }
                                    }
                                });
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void unsubscribePush() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.removeAll(Consts.PU_FOLLOWING,
                new ArrayList<ParseObject>(){{
                    add(ParseObject.createWithoutData(Consts.PROTEST, ProtestActivity.getId()));
                }});
        installation.saveInBackground(new SaveCallback() {
                                          @Override
                                          public void done(ParseException e) {
                                              if (e == null)
                                                  Log.d(TAG, "unsubscribed" + ProtestActivity.getId());
                                              else
                                                  Log.d(TAG, "failed to unsubscribed" + ProtestActivity.getId());
                                          }
                                      });
    }

    private void handleDelete() {
        deleteProtestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("Delete protest")
                        .setMessage("Are you sure you want to stop this protest?[:/]")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ParseObject toBeDeleted = ParseObject
                                        .createWithoutData(Consts.PROTEST, ProtestActivity.getId());
                                toBeDeleted.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d(TAG, "delete succeeded");
                                            Intent intent = new Intent(v.getContext(), ManageActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Log.d(TAG, "delete failed" + e.getMessage());
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
        });
    }

    private void initViews(View v) {
        joinThisCause = (Button) v.findViewById(R.id.joinThisCause);
        aboutText = (TextView) v.findViewById(R.id.aboutText);
        deleteProtestView = (Button) v.findViewById(R.id.deleteProtest);
        unfollowBtn = (Button) v.findViewById(R.id.unfollowBtn);
        unfollowBtn.setTypeface(Consts.appFont);
        progress = (ProgressBar) v.findViewById(R.id.progress);

        aboutText.setTypeface(Consts.appFont);
        deleteProtestView.setTypeface(Consts.appFont);
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
        Log.v(TAG, "switched fragment to about");
        checkAndSwitchIfFollow();
    }
}
