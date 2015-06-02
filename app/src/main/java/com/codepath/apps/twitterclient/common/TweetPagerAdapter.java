package com.codepath.apps.twitterclient.common;

import android.support.v4.app.Fragment;

import com.codepath.apps.twitterclient.fragment.HomeTimelineFragment;
import com.codepath.apps.twitterclient.fragment.MetionsTimelineFragment;

/**
 * Created by sjayaram on 5/31/2015.
 */
public class TweetPagerAdapter extends SmartFragmentStatePagerAdapter {
    private String tabtitles[] = {"Home" , "Mentions"};

    public TweetPagerAdapter(android.support.v4.app.FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new HomeTimelineFragment();
        else if(position == 1)
            return new MetionsTimelineFragment();
        else
            return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

    @Override
    public int getCount() {
        return tabtitles.length;
    }
}
