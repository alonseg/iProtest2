package com.alonseg.iprotest.adapters;

/**
 * Created by Alon on 9/4/2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class ProtestViewPagerAdapter extends FragmentStatePagerAdapter {

    public final String TAG = "PRTST_V_PGR_ADPTR";
    private CharSequence Titles[];
    private int NumbOfTabs;
    private List<Fragment> fragments;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ProtestViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabs,
                                   List<Fragment> fragments) {
        super(fm);

        this.fragments = fragments;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabs;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.Titles[position];
    }

    @Override
    public int getCount() {
        return this.NumbOfTabs;
    }
}
