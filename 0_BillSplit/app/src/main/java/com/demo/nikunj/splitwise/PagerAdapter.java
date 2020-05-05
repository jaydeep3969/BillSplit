package com.demo.nikunj.splitwise;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by OM on 3/10/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    //private String[] tabTitles =  {"Friends","Groups","Activity"};
    int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NoOfTabs)
    {
        super(fm);
        this.mNoOfTabs = NoOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0 :
                tab_friends friends = new tab_friends();
                return friends;
            case 1:
                tab_groups groups = new tab_groups();
                return  groups;
            case 2:
                tab_activity activity = new tab_activity();
                return  activity;
            default:
                tab_friends friends1 = new tab_friends();
                return  friends1;

        }

    }

    @Override
    public int getCount() {
        return this.mNoOfTabs;
    }

    /*public String getTabTitles(int position) {
        return tabTitles[position];
    }*/
}
