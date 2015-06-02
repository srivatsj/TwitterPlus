package com.codepath.apps.twitterclient.home;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.Profile.ProfileActivity;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.AlertDialogFragment;
import com.codepath.apps.twitterclient.common.EndlessScrollListener;
import com.codepath.apps.twitterclient.common.SmartFragmentStatePagerAdapter;
import com.codepath.apps.twitterclient.common.TweetPagerAdapter;
import com.codepath.apps.twitterclient.compose.ComposeActivity;
import com.codepath.apps.twitterclient.compose.ComposeFragment;
import com.codepath.apps.twitterclient.detail.DetailActivity;
import com.codepath.apps.twitterclient.detail.VideoDialogFragment;
import com.codepath.apps.twitterclient.fragment.HomeTimelineFragment;
import com.codepath.apps.twitterclient.fragment.MetionsTimelineFragment;
import com.codepath.apps.twitterclient.fragment.TweetsListFragment;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.reply.ReplyFragment;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class TimelineActivity extends ActionBarActivity implements ReplyFragment.OnFragmentInteractionListener{

    private final int REQUEST_CODE = 20;
    private final int DETAIL_REQUEST_CODE = 200;
    private ViewPager viewPager;
    TweetPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setCustomView(R.layout.home_actionbar_title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapterViewPager = new TweetPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);

            return true;
        }
        else if(id == R.id.action_profile){

            Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = data.getExtras().getParcelable("tweet");
            ((TweetsListFragment)adapterViewPager.getRegisteredFragment(viewPager.getCurrentItem())).insert(tweet);

        }
        else if(resultCode == RESULT_OK && requestCode == DETAIL_REQUEST_CODE) {
            if(data.getExtras().getParcelable("tweet") != null) {
                Tweet tweet = data.getExtras().getParcelable("tweet");
                ((TweetsListFragment)adapterViewPager.getRegisteredFragment(viewPager.getCurrentItem())).insert(tweet);
            }

            if(data.getExtras().getParcelable("org") != null) {
                Tweet tweet2 = data.getExtras().getParcelable("org");
                ((TweetsListFragment)adapterViewPager.getRegisteredFragment(0)).updateTweet(tweet2);
                ((TweetsListFragment)adapterViewPager.getRegisteredFragment(1)).updateTweet(tweet2);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Tweet tweet) {

        if(tweet != null){
            ((TweetsListFragment)adapterViewPager.getRegisteredFragment(0)).insert(tweet);
        }
    }

}
