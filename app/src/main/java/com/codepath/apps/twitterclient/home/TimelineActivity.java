package com.codepath.apps.twitterclient.home;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
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

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.AlertDialogFragment;
import com.codepath.apps.twitterclient.common.EndlessScrollListener;
import com.codepath.apps.twitterclient.compose.ComposeActivity;
import com.codepath.apps.twitterclient.compose.ComposeFragment;
import com.codepath.apps.twitterclient.detail.DetailActivity;
import com.codepath.apps.twitterclient.detail.VideoDialogFragment;
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

public class TimelineActivity extends ActionBarActivity implements ReplyFragment.OnFragmentInteractionListener {

    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private String maxId;
    private String sinceId;
    private final int REQUEST_CODE = 20;
    private final int DETAIL_REQUEST_CODE = 200;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        EventBus.getDefault().register(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setCustomView(R.layout.home_actionbar_title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        lvTweets = (ListView)findViewById(R.id.lvTweets);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                sinceId = "1";
                refreshTimeline();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                populateTimeline();
            }
        });

        populateTimeline();

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = (Tweet) parent.getItemAtPosition(position);

                Intent i = new Intent(TimelineActivity.this, DetailActivity.class);
                // put "extras" into the bundle for access in the second activity
                i.putExtra("tweet", tweet);
                startActivityForResult(i, DETAIL_REQUEST_CODE);
            }
        });

    }

    private void populateTimeline()
    {
        if(!Utils.isNetworkAvailable(this))
        {
            //Toast.makeText(this, "Getting from sql lite", Toast.LENGTH_SHORT).show();

            if(aTweets.getCount()==0) {
                List<Tweet> tweetList = Tweet.getAll();
                aTweets.clear();
                aTweets.addAll(tweetList);
            }
        }
        else {
            client.getHomeTimeline(maxId, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());

                    if(aTweets.getCount()==0) {
                        User.deleteAll();
                    }

                    ArrayList<Tweet> tweetList = Tweet.fromJsonArray(response);
                    maxId = tweetList.get(tweetList.size() - 1).getUid() - 1 + "";


                    aTweets.addAll(tweetList);

                    if (sinceId == null)
                        sinceId = aTweets.getItem(0).getUid() + "";

                    //Toast.makeText(TimelineActivity.this, "new " + tweetList.size() + " tweets, total " + aTweets.getCount() + " ,maxID " + maxId, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    private void refreshTimeline()
    {
        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        }
        else {
            client.getHomeTimeline(null, sinceId, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());
                    User.deleteAll();
                    ArrayList<Tweet> tweetList = Tweet.fromJsonArray(response);

                    if (tweetList.size() > 0) {
                        sinceId = tweetList.get(0).getUid() + "";

                    maxId = tweetList.get(tweetList.size() - 1).getUid() - 1 + "";

                    /*for (Tweet tweet : tweetList) {
                        aTweets.insert(tweet, 0);
                    }*/

                        aTweets.clear();
                        aTweets.addAll(tweetList);
                    }

                    //Toast.makeText(TimelineActivity.this, "sinceId " + sinceId, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
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

            Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            Tweet tweet = (Tweet)data.getExtras().getParcelable("tweet");
            aTweets.insert(tweet, 0);
            //Toast.makeText(this, tweet.toString(), Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == RESULT_OK && requestCode == DETAIL_REQUEST_CODE) {
            if(data.getExtras().getParcelable("tweet") != null) {
                Tweet tweet = (Tweet) data.getExtras().getParcelable("tweet");
                aTweets.insert(tweet, 0);
                //Toast.makeText(this, tweet.toString(), Toast.LENGTH_SHORT).show();
            }

            if(data.getExtras().getParcelable("org") != null) {
                Tweet tweet2 = (Tweet) data.getExtras().getParcelable("org");

                for(Tweet tweet : tweets){
                    if(tweet2.getUid() == tweet.getUid())
                    {
                        //tweet = tweet2;
                        tweet.setFavorited(tweet2.isFavorited());
                        tweet.setRetweeted(tweet2.isRetweeted());
                        tweet.setFavouritesCount(tweet2.getFavouritesCount());
                        tweet.setRetweetCount(tweet2.getRetweetCount());
                        break;
                    }
                }

                aTweets.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // This method will be called when a MessageEvent is posted
    public void onEvent(Tweet event){
        //Toast.makeText(this, event.getBody() + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Tweet tweet) {

        if(tweet != null){
            aTweets.insert(tweet, 0);
            //Toast.makeText(this, tweet.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
