package com.codepath.apps.twitterclient.Profile;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.fragment.UserFollowersFragment;
import com.codepath.apps.twitterclient.fragment.UserFollowingsFragment;
import com.codepath.apps.twitterclient.fragment.UserTimelineFragment;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends ActionBarActivity {

    User user;
    UserFollowingsFragment userFollowingsFragment;

    UserFollowersFragment userFollowersFragment;
    UserTimelineFragment userTimelineFragment;
    protected TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = TwitterApplication.getRestClient();

        user = getIntent().getParcelableExtra("user");
        if(user == null) {
            user = Utils.getLoggedInUser();
            //getUser(user);
        }else
        {
            //getUser(user);
        }

        String screenName = user.getScreenName();
        //getSupportActionBar().setTitle("@" + screenName);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setTitle(" Profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateProfileHeader(user);


        userTimelineFragment =  UserTimelineFragment.newInstance(screenName);

        if(savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }

    }

    public void getUser(User user) {

        if(!Utils.isNetworkAvailable(this))
        {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_SHORT).show();

        }
        else {
            client.getUserObj(user.getScreenName(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    User user = User.fromJson(response);
                    populateProfileHeader(user);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    public void populateProfileHeader(final User user){
        TextView tvName = (TextView)findViewById(R.id.tvUser);
        TextView tvTag = (TextView)findViewById(R.id.tvTag);
        TextView tvFollwerCount = (TextView)findViewById(R.id.tvFollowers);
        TextView tvFollowingCount = (TextView)findViewById(R.id.tvFollowing);
        TextView tvTweetCount = (TextView)findViewById(R.id.tvTweetCount);
        ImageView ivImage = (ImageView)findViewById(R.id.ivProfile);
        ImageView ivBg = (ImageView)findViewById(R.id.ivBg);

        tvName.setText(user.getName());
        tvTag.setText(user.getTagLine());
        tvFollowingCount.setText(Utils.countToWords(user.getFollowingCount()) + " Following");
        tvFollwerCount.setText(Utils.countToWords(user.getFollowersCount()) + " Followers");
        tvTweetCount.setText(Utils.countToWords(user.getStatuses_count()) + " Tweets");
        Picasso.with(this).load(user.getProfileImageUrl()).fit().into(ivImage);
        Picasso.with(this).load(user.getProfileBackgroundImg()).fit().into(ivBg);

        tvTweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userTimelineFragment == null) {
                    userTimelineFragment =  UserTimelineFragment.newInstance(user.getScreenName() + "");
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContainer, userTimelineFragment);
                ft.addToBackStack("one");
                ft.commit();

            }

        });

        tvFollwerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userFollowersFragment == null) {
                    userFollowersFragment =  UserFollowersFragment.newInstance(user.getUid() + "");
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContainer, userFollowersFragment);
                ft.addToBackStack("two");
                ft.commit();

            }
        });

        tvFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userFollowingsFragment == null) {
                    userFollowingsFragment =  UserFollowingsFragment.newInstance(user.getUid() + "");
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContainer, userFollowingsFragment);
                ft.addToBackStack("three");
                ft.commit();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
