package com.demo.nikunj.splitwise;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by OM on 3/10/2018.
 */

public class PagerAdapterSplit extends FragmentStatePagerAdapter {

    //private String[] tabTitles =  {"Friends","Groups","Activity"};
    int mNoOfTabs;

    public PagerAdapterSplit(FragmentManager fm, int NoOfTabs)
    {
        super(fm);
        this.mNoOfTabs = NoOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0 :
                tab_equally equally = new tab_equally();
                return equally;
            case 1:
                tab_exact exact = new tab_exact();
                return  exact;
            case 2:
                tab_percentage percentage = new tab_percentage();
                return  percentage;
            case 3:
                tab_share share = new tab_share();
                return  share;
            case 4:
                tab_adjustment adjustment = new tab_adjustment();
                return  adjustment;
            default:
                tab_equally equally1 = new tab_equally();
                return  equally1;

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
