package com.alonseg.iprotest.Tabs.ProtestTabs;

/**
 * Created by Alon on 9/4/2015.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Interfaces.FragmentInterface;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Step;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.adapters.StepsAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.util.ArrayList;

public class NextTab extends BaseProtestTab implements FragmentInterface{

    public final String TAG = "NEXT";
    public Button joinThisCause;
    public ImageButton nextDone;
    public CheckBox nextCheck;
    public EditText nextEdit;
    public RelativeLayout newNextRel;
    public ListView nextListView;

    public ArrayList<Step> stepsListArr = new ArrayList<>();
    public StepsAdapter stepsAdapter;
    public Context con;
    private View vView;
    private boolean created = false;

    public static boolean sendPush = false;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_next, container, false);
        vView = v;
        con = v.getContext();
        initViews(v);

        handleJoinBtn(joinThisCause, con, TAG);

        handleDone();

        handleNewNextStep();

        handleStepList(v);

        created = true;

        return v;
    }

    private void handleDone() {
        nextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextEdit.getText().toString().equals("")) {
                    nextEdit.setError("Try writing something first [:|]");
                    return;
                }
//                for (char myChar: nextEdit.getText().toString().toCharArray()) {
//                    if (!Character.isAlphabetic(myChar) && !Character.isDigit(myChar) || Character.isLetter()){
//                        nextEdit.setError("Use letters and numbers only [:|]");
//                        return;
//                    }
//                }

                final String message = nextEdit.getText().toString().trim();

                if(!checkIfChecked(v))
                    nextEdit.setText("");



                final ParseObject newStep = new ParseObject(Consts.STEP);
                newStep.put(Consts.STEP_MSG, message);
                newStep.put(Consts.STEP_CREATED_BY, app.getUsername());
                newStep.put(Consts.STEP_PROTEST_ID, ProtestActivity.getId());
                newStep.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);
                            query.getInBackground(ProtestActivity.getId(), new GetCallback<ParseObject>() {
                                public void done(final ParseObject protestToUpdate, ParseException e) {
                                    protestToUpdate.add(Consts.P_STEP, newStep);
                                    protestToUpdate.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                fragmentBecameVisible();
                                                if (!sendPush)
                                                    return;
                                                sendPush(message);

                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void sendPush(String message) {
        // Create our Installation query
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereContainsAll(Consts.PU_FOLLOWING,
                new ArrayList<ParseObject>(){{
                    add(ParseObject.createWithoutData(Consts.PROTEST,
                            ProtestActivity.getId()));
                }});
        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage(ProtestActivity.getName() + " - " + message);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Log.d(TAG, "push sent");
                else
                    Log.d(TAG, "failed to send push " + e.getMessage());
            }
        });
    }

    private boolean checkIfChecked(View v) {
        if (nextCheck.isChecked()){
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Send push notification?")
                    .setMessage("Are you sure you want to send a notification?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sendPush = true;
                            nextEdit.setText("");
                            nextCheck.toggle();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sendPush = false;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
        return nextCheck.isChecked();
    }

    private void handleNewNextStep() {
        if (!ProtestActivity.isAdmin())
            return;
        newNextRel.setVisibility(View.VISIBLE);
    }

    private void initViews(View v) {
        joinThisCause = (Button) v.findViewById(R.id.joinThisCause);
        nextDone = (ImageButton) v.findViewById(R.id.nextDone);
        nextCheck = (CheckBox) v.findViewById(R.id.nextCheckBox);
        nextEdit = (EditText) v.findViewById(R.id.nextEditText);
        newNextRel = (RelativeLayout) v.findViewById(R.id.newNextStep);
        nextListView = (ListView) v.findViewById(R.id.nextListView);
    }

//    private void handleJoin(View v) {
//
//
//        joinThisCause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                followTheProtest(TAG, con);
//                checkAndSwitchIfFollow();
//            }
//        });
//
//        checkAndSwitchIfFollow();
//    }

    private void handleStepList(final View v) {
        v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        //get the steps list and display it
        ParseQuery<ParseObject> query = new ParseQuery<>(Consts.PROTEST);
        query.include(Consts.P_STEP);
        query.include(Consts.POST_CREATION_TIME);

        query.orderByAscending("createdAt");
        query.getInBackground(ProtestActivity.getId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                stepsListArr = Step.convertListObj(parseObject.getList(Consts.P_STEP));

                stepsAdapter = new StepsAdapter(v.getContext(), R.layout.row_post, stepsListArr);
                nextListView.setAdapter(stepsAdapter);
                nextListView.setVisibility(View.VISIBLE);
                v.findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });
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
        Log.v(TAG, "switched fragment to next");
        if (!created){

        }
        checkAndSwitchIfFollow();
        handleNewNextStep();

        handleStepList(vView);

    }

}
