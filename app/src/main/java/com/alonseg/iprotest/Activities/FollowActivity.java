package com.alonseg.iprotest.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.adapters.ProtestPostAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class FollowActivity extends FragmentActivity {

    public Button logOrReg;
    public Button logOut;
    public TextView emptyText;
    public TextView title;
    public ListView followListView;

    public static ArrayList<Post> followPostArrList = new ArrayList<>();
    public static ProtestPostAdapter followPostAdapter;

    public Context con;

    public static String TAG = "FOLLOWING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        con = this;

        initViews();

        handleLogOrRegBtn();

        handleNotLoggedMsg();

        handleFollowPostList();

        setBottomButtons();

        Button  locationBtn = (Button) findViewById(R.id.mapBtn);
        locationBtn.setVisibility(View.GONE);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, addLocation.class);
                startActivity(intent);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.logOut();
            }
        });
    }

    private void initViews() {
        logOrReg = (Button) findViewById(R.id.followLogReg);
        logOut = (Button) findViewById(R.id.logOut);
        logOut.setTypeface(Consts.appFont);
        emptyText = (TextView) findViewById(R.id.followEmptyText);
        emptyText.setTypeface(Consts.appFont);
        followListView = (ListView) findViewById(R.id.followListPosts);
        title = (TextView) findViewById(R.id.followingTtl);
        title.setTypeface(Consts.appFont);
    }

    private void handleFollowPostList() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.POST);
        query.whereContainedIn(Consts.P_ID, app.getFollowingList());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedPostList, ParseException e) {
                if (e == null) {
                    if (retrievedPostList.size() > 0)
                    {
                        emptyText.setVisibility(View.GONE);
                    }else {
                        if (app.logged()) {
                            emptyText.setText(getString(R.string.reg_but_empty_following));
                            emptyText.setTypeface(Consts.appFont);
                            emptyText.setVisibility(View.VISIBLE);
                        }
                    }
                    followPostArrList = Post.convertListParseObj(retrievedPostList);
                    final Map<String, Protest> protests = ProtestPostAdapter.getProtestsForPosts(followPostArrList);
                    Collections.sort(followPostArrList);
                    followPostAdapter = new ProtestPostAdapter(con,
                            R.layout.row_post, followPostArrList, protests);
                    followListView.setAdapter(followPostAdapter);

                } else {
                    Log.v(TAG, "Error: " + e.getMessage());
                }
                findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });
    }

    private void handleLogOrRegBtn() {
        if (app.logged()){
            logOrReg.setVisibility(View.GONE);
        }else {
            logOrReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(con, LoginActivity.class);

                    intent.putExtra("opp", getString(R.string.asked_to_reg_log));
                    intent.putExtra("ttl", "HURRAY!");
                    startActivityForResult(intent, Consts.REQ_SIGN);
                }
            });
        }
    }

    private void setBottomButtons() {
        ImageButton followBtn = (ImageButton) this.findViewById(R.id.btnFollow);
        ImageButton myAreaBtn = (ImageButton) this.findViewById(R.id.btnManage);
        ImageButton exploreBtn = (ImageButton) this.findViewById(R.id.btnExplore);

        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExploreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        myAreaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ManageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(con, "You Are Here!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNotLoggedMsg() {
        if (!app.logged()){
            logOrReg.setVisibility(View.VISIBLE);
            emptyText.setText(getString(R.string.unregistered_follow));
            emptyText.setTypeface(Consts.appFont);
            emptyText.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Consts.REQ_SIGN){
            if (resultCode == Consts.REQ_SIGN_SUC){
                handleLogOrRegBtn();

                handleNotLoggedMsg();

                handleFollowPostList();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleLogOrRegBtn();
        handleNotLoggedMsg();
        handleFollowPostList();
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
