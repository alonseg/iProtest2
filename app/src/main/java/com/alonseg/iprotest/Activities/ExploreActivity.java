package com.alonseg.iprotest.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.adapters.ProtestPostAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ExploreActivity extends FragmentActivity {

    public static String TAG = "EXPLORE";

    public static ArrayList<Post> explorePostArrList;
    public static ProtestPostAdapter explorePostAdapter;

    public static ListView explorePostListView;
    public ProgressBar progress;
    public ImageButton qrBtn;


    public Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        cont = this;

        initViews();

        handleSearch();

        handleQR();

        handleExplorePosts();

        setBottomButtons();
    }

    private void initViews() {
        explorePostListView = (ListView) findViewById(R.id.explorePostsList);
        progress = (ProgressBar) findViewById(R.id.progress);
        qrBtn = (ImageButton) findViewById(R.id.btnQR);

    }

    private void handleQR() {
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ExploreActivity.this);
                integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
                //customize the prompt message before scanning
                integrator.addExtra("PROMPT_MESSAGE", "Where Is That Protest?!");
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
            }
        });

    }

    private void handleSearch() {

        SearchView sv = (SearchView) findViewById(R.id.searchTopic);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progress.setVisibility(View.VISIBLE);
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Consts.POST);
                List<String> arrayList = Arrays.asList(query.trim().split("\\s+"));
                parseQuery.whereContainsAll(Consts.SEARCH_TAGS, arrayList);
                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        setPostsList(objects, e);
                        progress.setVisibility(View.GONE);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void handleExplorePosts() {
        progress.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.POST);
        //TODO filter somehow. currently it get all of the protests
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> retrievedPostList, ParseException e) {
                setPostsList(retrievedPostList, e);
            }
        });
    }

    private void setPostsList(List<ParseObject> retrievedPostList, ParseException e) {
        if (e == null) {
            explorePostArrList = Post.convertListParseObj(retrievedPostList);
            final Map<String, Protest> protests = ProtestPostAdapter.getProtestsForPosts(explorePostArrList);
            explorePostAdapter = new ProtestPostAdapter(cont,
                    R.layout.row_protest_post, explorePostArrList, protests);
            explorePostListView.setAdapter(explorePostAdapter);
            findViewById(R.id.progress).setVisibility(View.GONE);
        } else {
            Log.v(TAG, "Error: " + e.getMessage());
        }
    }

    private void setBottomButtons() {
        ImageButton messageBtn = (ImageButton) this.findViewById(R.id.btnFollow);
        ImageButton myAreaBtn = (ImageButton) this.findViewById(R.id.btnManage);
        ImageButton exploreBtn = (ImageButton) this.findViewById(R.id.btnExplore);

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FollowActivity.class);
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

        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                if (contents == null)
                    return;
                // Handle successful scan
                String[] dataParts = contents.split("/", -1);
                if (dataParts == null || dataParts.length < Consts.ID_INDEX + 1){
                    Toast.makeText(cont, "Incompatible QR", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Intent startIntent = new Intent(cont, ProtestActivity.class);
                    if (dataParts[Consts.TYPE_INDEX].equals(Consts.PROTEST)) {
                        startIntent.putExtra(Consts.P_ID, dataParts[Consts.ID_INDEX]);
                    } else {
                        startIntent.putExtra(Consts.TYPE, dataParts[Consts.TYPE_INDEX]);
                        startIntent.putExtra(Consts.ID, dataParts[Consts.ID_INDEX]);
                    }
                    startActivity(startIntent);
                    return;

                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.i("App","Scan unsuccessful");
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        handleExplorePosts();
    }
}