package com.alonseg.iprotest.Activities;

import android.app.Application;
import android.content.Context;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alon on 11/9/2015.
 */
public class app extends Application {

    private static final String TAG = "app";
    public static Context appCon;
    private static ParseUser currentUser;



    @Override
    public void onCreate() {
        super.onCreate();
        appCon = this.getApplicationContext();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "UOu89QKEFy3KapBdIxjsRYyvF1ufh4BAtHpe1Kck",
                "sulyIALglZ1ey6GSy8DpCbegQQUY6NRWseeNCGKV");
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "saved curr installation");
                } else {
                    Log.d(TAG, "failed to save curr installation" + e.getMessage());
                }
            }
        });

        try {
            currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                currentUser = currentUser.fetch();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static boolean logged() {
        return app.currentUser != null;
    }

    public static void logOut(){
        ParseUser.logOut();
        currentUser = ParseUser.getCurrentUser();


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.remove(Consts.PU_FOLLOWING);
        installation.saveInBackground();

    }

    public static String getUsername(){
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public static List<String> getFollowingList(){
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null)
            return new ArrayList<>();
        List<Object> pre = currentUser.getList(Consts.PU_FOLLOWING);
        if (pre == null )
            return new ArrayList<>();
        String[] ret = new String[pre.size()];
        return currentUser.getList(Consts.PU_FOLLOWING);
    }

    public static void initUser(){
        currentUser = ParseUser.getCurrentUser();
    }

    public static void addUnique(String whereToAdd, String whatToAdd){
        currentUser.addUnique(whereToAdd, whatToAdd);
        currentUser.saveInBackground();
        Log.d(TAG, "whereToAdd = " + whereToAdd + " whatToAdd = " + whatToAdd);
    }

    public static void addUniqueSync(String whereToAdd, String whatToAdd) {
        currentUser.addUnique(whereToAdd, whatToAdd);
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void removeAll(String puFollowing, ArrayList<String> strings) {
        currentUser.removeAll(puFollowing,strings);
        currentUser.saveInBackground();
    }
}
