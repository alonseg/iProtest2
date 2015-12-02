package com.alonseg.iprotest.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Interfaces.FragmentInterface;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Post;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.Objects.Step;
import com.alonseg.iprotest.R;
import com.alonseg.iprotest.Tabs.ProtestTabs.AboutTab;
import com.alonseg.iprotest.Tabs.ProtestTabs.BaseProtestTab;
import com.alonseg.iprotest.Tabs.ProtestTabs.NewsTab;
import com.alonseg.iprotest.Tabs.ProtestTabs.NextTab;
import com.alonseg.iprotest.Tabs.ProtestTabs.PhotosTab;
import com.alonseg.iprotest.adapters.ProtestViewPagerAdapter;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ProtestActivity extends FragmentActivity {
    public static final String TAG = "PROTEST_ACTV";


    public static Intent intent;
    private static Protest protest;
    public String protestName = "no name";
    public String protestID;
    public String protestAdmin;
    public boolean following;
    public boolean openToPost = false;

    public TextView protestNameView;
    public TextView protestFollowCount;
    public ImageView protestFollowImage;
    public static ProgressBar progress;

    public ViewPager pager;
    public ProtestViewPagerAdapter adapter;
    public SlidingTabLayout tabs;
    public CharSequence Titles[] = new CharSequence[4];//={"News","Photos","About", "What's Next?"};
    public int NumOfTabs =4;

    public static Context con;

    public static Object getAdmin() {
        return protest.getAdmin();
    }

    public static String getDescription() {
        return protest != null ? protest.getDescription() : null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protest);
        con = this;

        initViews();

        handlePager();

        handleTabs();

        setBottomButtons();

        handleProtestData(getIntent());

        progress.setVisibility(View.GONE);

//        pager.setCurrentItem(2);
    }

    private void initViews() {
        protestNameView = (TextView) findViewById(R.id.protestTitle);
        protestFollowCount = (TextView) findViewById(R.id.protestFollowCount);
        protestFollowImage = (ImageView) findViewById(R.id.protestFollowImage);
        progress = (ProgressBar) findViewById(R.id.progress);
        Titles[0] = getResources().getString(R.string.news);
        Titles[1] = getResources().getString(R.string.photos);
        Titles[2] = getResources().getString(R.string.about);
        Titles[3] = getResources().getString(R.string.next);

        // Creating The ProtestViewPagerAdapter and Passing Fragment Manager, Titles for the Tabs and Number Of Tabs.

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(BaseProtestTab.instantiate(this, NewsTab.class.getName()));
        fragments.add(BaseProtestTab.instantiate(this, PhotosTab.class.getName()));
        fragments.add(BaseProtestTab.instantiate(this, AboutTab.class.getName()));
        fragments.add(BaseProtestTab.instantiate(this, NextTab.class.getName()));
        adapter =  new ProtestViewPagerAdapter(getSupportFragmentManager(),Titles, NumOfTabs, fragments);
    }

    private void handleProtestData(Intent intent) {
        Intent startIntent = new Intent(con, ProtestActivity.class);
        if (intent.hasExtra(Consts.PROTEST)){
            protest = intent.getParcelableExtra(Consts.PROTEST);

        }else if (intent.hasExtra(Consts.P_ID)){
            protestID = intent.getStringExtra(Consts.P_ID);
        }else if (intent.hasExtra(Consts.TYPE)){
            try {
                ParseQuery<ParseObject> query;
                switch (intent.getStringExtra(Consts.TYPE)){
                    case Consts.STEP:
                        query = ParseQuery.getQuery(Consts.STEP);
                        Step step = new Step(query.get(intent.getStringExtra(Consts.ID)));
                        protestID = step.getProtestID();

                        break;
                    case Consts.POST:
                        query = ParseQuery.getQuery(Consts.POST);
                        Post post = new Post(query.get(intent.getStringExtra(Consts.ID)));
                        protestID = post.getProtestID();
                        startIntent = new Intent(con, SinglePostDialog.class);
                        startIntent.putExtra(Consts.POST, post);
                        openToPost = true;
                        con.startActivity(intent);

                        break;
                }
            } catch (ParseException e) {
                Log.d(TAG, "failed " + e.getMessage());
            }
        }else
            Log.v(TAG, "activity started without a protest obj");

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);
        try {
            protest = new Protest(query.get(protestID));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        protestAdmin = protest.getAdmin();
        protestName = protest.getName();
        protestID = protest.getItemId();
        Consts.setTextToView(protestNameView, protestName);

        handleCounter();
        if (openToPost)
            startActivity(startIntent);
    }

    private void handleCounter() {
        int count = protest.getCount();
        Consts.setTextToView(protestFollowCount, String.valueOf(count));
        if (count > 1) {
            if (count > Consts.LOTS) {
                protestFollowImage.setImageResource(R.drawable.stick_man_lots);
            } else
                protestFollowImage.setImageResource(R.drawable.stick_man_few);
        }else
            protestFollowImage.setImageResource(R.drawable.stick_man_one);

        protestFollowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Already " + protestFollowCount.getText().toString() +
                        " are following!", Toast.LENGTH_SHORT).show();
            }
        });

        protestFollowCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Already " + protestFollowCount.getText().toString() +
                        " are following!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private void handleTabs() {
        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.protestTabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.appBackground);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    private void handlePager() {
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.protestPager);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float v, final int i2) {
            }

            @Override
            public void onPageSelected(final int position) {
                FragmentInterface fragment = (FragmentInterface) adapter.instantiateItem(pager, position);
                if (fragment != null) {
                    fragment.fragmentBecameVisible();
                }
            }

            @Override
            public void onPageScrollStateChanged(final int position) {
            }
        });
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
                Intent intent = new Intent(v.getContext(), ExploreActivity.class);
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

    public static String getName(){
        return protest == null ? null : protest.getName();
    }

    public static String getId(){
        return protest == null ? null : protest.getItemId();
    }

    public static boolean isAdmin(){
        if (!app.logged())
            return false;
        return protest != null && protest.getAdmin().equals(app.getUsername());
    }

    public static boolean amIFollowing(){
        return protest.amIFollowing();
    }

}
