package com.alonseg.iprotest.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.adapters.ProtestsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ManageActivity extends FragmentActivity {

    public static final String TAG = "MANAGE";

    public static ArrayList<Protest> manageProtestArrList = new ArrayList<>();
    public static ProtestsAdapter manageProtestAdapter;
    public boolean firstTime = true;

    private RelativeLayout manageBody;
    private RelativeLayout manageNewProtest;
    private ListView manageProtestList;
    private TextView manageNotLoggedText;
    private TextView manageTtl;
    private TextView noProtestsOnManageText;
    private Button logOrReg;
    private ImageButton newProtestBtn;
    private Button doneBtn;

    private EditText pNameEdit;
    private EditText pDescEdit;

    private Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = this;

        setContentView(R.layout.activity_manage);

        app.initUser();

        findViews();

        handleLogOrReg();

        startManageContent();

        handleNewProtest();

        setBottomButtons();
    }

    private void handleNewProtest() {
        handleDone();

        newProtestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchManagePage();
            }
        });
    }

    private void handleDone() {
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!app.logged()){
                    Intent intent = new Intent(con, LoginActivity.class);

                    intent.putExtra("opp", getString(R.string.unregistered_msg_part_1) +
                            " Start a new Protest! " + getString(R.string.unregistered_msg_part_3));
                    intent.putExtra("ttl", "UH OH...");
                    startActivityForResult(intent, Consts.REQ_SIGN);
                    return;
                }
                String pName = pNameEdit.getText().toString();

                if (TextUtils.isEmpty(pName)) {
                    pNameEdit.setError(getString(R.string.error_field_required));
                    return;
                } else if (!Character.isLetterOrDigit(pName.charAt(0))) {
                    pNameEdit.setError(Html.fromHtml("<font color='red'>Error Message!</font>"));//.setError(getString(R.string.error_letter_num));
                    return;
                }
                pName = pName.trim();

                String pDesc = pDescEdit.getText().toString().trim();
                if (TextUtils.isEmpty(pDesc)) {
                    pNameEdit.setError(getString(R.string.error_field_required));
                    return;
                }
                pNameEdit.setText("");
                pDescEdit.setText("");

                final ParseObject newProtest = new ParseObject(Consts.PROTEST);

                newProtest.put(Consts.P_NAME, pName);
                newProtest.put(Consts.P_DESCRIPTION, pDesc);
                newProtest.put(Consts.P_ADMIN, app.getUsername());
                newProtest.addUnique(Consts.P_FOLLOWERS_LIST, app.getUsername());
                newProtest.increment(Consts.P_FOLLOWERS_COUNT);
                newProtest.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            app.addUnique(Consts.PU_FOLLOWING, newProtest.getObjectId());
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();

                            installation.addUnique(Consts.PU_FOLLOWING,
                                    ParseObject.createWithoutData(Consts.PROTEST, newProtest.getObjectId()));
                            installation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null)
                                        Log.d(TAG, "added protest to push list");
                                    else
                                        Log.d(TAG, "failed to add protest to push list");
                                }
                            });
                            startManageContent();
                            switchManagePage();
                        } else {
                            Log.v("SAVE_ERR", e.toString() + e.getCode());
                            if (e.getMessage().equals("DUPLICATE")) {
                                pNameEdit.setError(getString(R.string.error_protest_name_taken));
                            }
                        }
                    }
                });
            }
        });
    }

    private void switchManagePage(){
        int vis = manageBody.getVisibility();
        manageBody.setVisibility(manageNewProtest.getVisibility());
        manageNewProtest.setVisibility(vis);
    }

    private void findViews() {
        manageProtestList = (ListView) findViewById(R.id.manageListProtests);
        manageNotLoggedText = (TextView) findViewById(R.id.manageNotLoggedText);
        noProtestsOnManageText = (TextView) findViewById(R.id.manageNoProtests);
        manageTtl = (TextView) findViewById(R.id.manageTtl);
        manageTtl.setTypeface(Consts.appFont);
        noProtestsOnManageText.setTypeface(Consts.appFont);
        logOrReg = (Button) findViewById(R.id.manageLogReg);
        newProtestBtn = (ImageButton) findViewById(R.id.manageNewProtestBtn);
        manageBody = (RelativeLayout) findViewById(R.id.managePageBody);
        manageNewProtest = (RelativeLayout) findViewById(R.id.managePageNewProtestLayout);
        pNameEdit = (EditText) findViewById(R.id.enterProtestTitle);
        pNameEdit.setTypeface(Consts.appFont);
        pDescEdit = (EditText) findViewById(R.id.enterProtestDescription);
        pDescEdit.setTypeface(Consts.appFont);
        doneBtn = (Button) findViewById(R.id.enterDone);
        ((TextView)findViewById(R.id.addProtestTtl)).setTypeface(Consts.appFont);
        ((Button)findViewById(R.id.enterDone)).setTypeface(Consts.appFont);

    }

    private void handleLogOrReg() {
        logOrReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, LoginActivity.class);
                intent.putExtra("opp", getString(R.string.asked_to_reg_log));
                intent.putExtra("ttl", "HURRAY!");
                startActivityForResult(intent, Consts.REQ_SIGN);
            }
        });
        logOrReg.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleLogOrReg();

        startManageContent();

        handleNewProtest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Consts.REQ_SIGN){
            if (resultCode == Consts.REQ_SIGN_SUC){
                handleLogOrReg();

                startManageContent();

                handleNewProtest();
            }

        }
    }

    private void startManageContent() {
        //if you're logged
        if (app.logged()) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            manageNotLoggedText.setVisibility(View.GONE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);
            query.whereEqualTo(Consts.P_ADMIN, app.getUsername());
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> retrievedProtestList, ParseException e) {
                    if (e == null) {
                        manageProtestArrList = Protest.convertList(retrievedProtestList);
                        manageProtestAdapter = new ProtestsAdapter(
                                con,R.layout.row_protest, manageProtestArrList);
                        manageProtestList.setAdapter(manageProtestAdapter);
                        showHideNoProtestsMsg(retrievedProtestList);
                    } else {
                        Log.v(TAG, "Error: " + e.getMessage());
                    }
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }
            });
        }else{
            notLogged("view protests you manage ", getString(R.string.unregistered_manage));
        }
    }

    private void notLogged(String action, String message) {
        findViewById(R.id.progress).setVisibility(View.GONE);
        logOrReg.setVisibility(View.VISIBLE);
        manageProtestList.setVisibility(View.GONE);
        noProtestsOnManageText.setVisibility(View.GONE);
        manageNotLoggedText.setText(message);
        manageNotLoggedText.setVisibility(View.VISIBLE);
        manageNotLoggedText.setTypeface(Consts.appFont);

        if (firstTime) {
            firstTime = false;
            Intent intent = new Intent(con, LoginActivity.class);

            intent.putExtra("opp", getString(R.string.unregistered_msg_part_1) +
                    action + getString(R.string.unregistered_msg_part_3));
            intent.putExtra("ttl", "UH OH...");
            startActivityForResult(intent, Consts.REQ_SIGN);
        }
    }

    private void showHideNoProtestsMsg(List<ParseObject> retrievedProtestList) {
        noProtestsOnManageText.setTypeface(Consts.appFont);
        if (retrievedProtestList.size() == 0){
            noProtestsOnManageText.setVisibility(View.VISIBLE);
            manageProtestList.setVisibility(View.GONE);
        }
        else{
            noProtestsOnManageText.setVisibility(View.GONE);
            manageProtestList.setVisibility(View.VISIBLE);
            manageProtestAdapter.notifyDataSetChanged();
        }
    }

    private void setBottomButtons() {
        (this.findViewById(R.id.btnManage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        (this.findViewById(R.id.btnExplore)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExploreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        (this.findViewById(R.id.btnFollow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FollowActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_suggestion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
