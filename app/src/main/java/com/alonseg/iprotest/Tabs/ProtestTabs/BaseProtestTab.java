package com.alonseg.iprotest.Tabs.ProtestTabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.alonseg.iprotest.Activities.LoginActivity;
import com.alonseg.iprotest.Activities.ProtestActivity;
import com.alonseg.iprotest.Activities.app;
import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.Objects.Protest;
import com.alonseg.iprotest.R;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Alon on 11/11/2015.
 */
public class BaseProtestTab extends android.support.v4.app.Fragment {
    public ProgressBar progress;


    public static BaseProtestTab newInstance(String name) {
        BaseProtestTab fragment = new BaseProtestTab();
        Bundle arg = new Bundle(1);
        arg.putString("name", name);
        fragment.setArguments(arg);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void followTheProtest(final String tag, Context startCon){
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Consts.PROTEST);

        // Retrieve the object by id
        try {
            final ParseObject protestToUpdate = query.get(ProtestActivity.getId());
            protestToUpdate.increment(Consts.P_FOLLOWERS_COUNT);
            protestToUpdate.addUnique(Consts.P_FOLLOWERS_LIST,
                    app.getUsername());
            protestToUpdate.save();

            //update user fields
            app.addUniqueSync(Consts.PU_FOLLOWING, ProtestActivity.getId());

            ParseInstallation installation = ParseInstallation.getCurrentInstallation();

            installation.addUnique(Consts.PU_FOLLOWING,
                    ParseObject.createWithoutData(Consts.PROTEST, ProtestActivity.getId()));
            installation.saveInBackground();

            restartProtest(tag, protestToUpdate);
        } catch (ParseException e) {
            Log.d(tag, "something went wrong " + e.getMessage());
        }
    }

    protected void restartProtest(String tag, ParseObject protestToUpdate) {
        Intent intent = new Intent(getActivity(), ProtestActivity.class);
        intent.putExtra(Consts.PROTEST, new Protest(protestToUpdate));
        getActivity().finish();
        getActivity().startActivity(intent);
        Log.d(tag, "finished activity");
    }

    protected void handleJoinBtn(Button joinBtn, final Context con, final String TAG) {
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.logged()) {
                    followTheProtest(TAG, con);
                    checkAndSwitchIfFollow();
                } else {
                    Intent intent = new Intent(con, LoginActivity.class);

                    intent.putExtra("opp", getString(R.string.unregistered_msg_part_1) +
                            "Join A Cause! " + getString(R.string.unregistered_msg_part_3));
                    intent.putExtra("ttl", "UH OH...");
                    startActivity(intent);
                }
            }
        });
    }

    protected void checkAndSwitchIfFollow(){}
}
