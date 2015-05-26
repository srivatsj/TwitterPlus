package com.codepath.apps.twitterclient.detail;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.LinkifiedTextView;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.home.TimelineActivity;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.reply.ReplyFragment;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends ActionBarActivity implements ReplyFragment.OnFragmentInteractionListener {

    private TwitterClient client;
    private ImageView ivTweetMedia;
    private TextView tvUsername;
    private TextView tvScreename;
    private TextView tvBody;
    private TextView tvDate;
    private TextView tvRetweetCount;
    private TextView tvStarCount;
    private ImageView ivProfile;
    private ImageView ivPlay;
    private TextView tvReply;
    private Tweet reply;
    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        client = TwitterApplication.getRestClient();
        ivTweetMedia = (ImageView)findViewById(R.id.ivTweetMedia);
        tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvScreename = (TextView)findViewById(R.id.tvScreename);
        tvBody = (TextView)findViewById(R.id.tvBody);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvRetweetCount = (TextView)findViewById(R.id.tvRetweetCount);
        tvStarCount = (TextView)findViewById(R.id.tvStarCount);
        ivProfile = (ImageView)findViewById(R.id.ivProfile);
        ivPlay = (ImageView)findViewById(R.id.ivPlay);
        tvReply = (TextView)findViewById(R.id.tvReply);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tweet = (Tweet)getIntent().getParcelableExtra("tweet");

        tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = (DetailActivity.this).getFragmentManager();
                ReplyFragment diag = ReplyFragment.newInstance(tweet);

                if (diag.getDialog() != null) {
                    diag.getDialog().setCanceledOnTouchOutside(true);
                }
                diag.show(fm, "");

            }
        });

        tvUsername.setText(tweet.getUser().getName());
        tvScreename.setText("@" + tweet.getUser().getScreenName());
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfile);

        tvBody.setText(tweet.getBody());
        tvBody.setAutoLinkMask(0);
        Utils.linkifyText(tvBody);
        tvBody.setAutoLinkMask(1);

        tvDate.setText(Utils.convertToTime(tweet.getCreatedAt()));
        tvRetweetCount.setText(tweet.getRetweetCount());//Long.valueOf(tweet.getUid()).toString()
        tvStarCount.setText(tweet.getFavouritesCount());

        if(tweet.getImageUrl() != null) {
            Picasso.with(this).load(tweet.getImageUrl()).into(ivTweetMedia);

            if (tweet.getMediaType().equals("video")) {
                ivPlay.setVisibility(View.VISIBLE);

                ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fm = ((Activity) DetailActivity.this).getFragmentManager();
                        VideoDialogFragment diag = VideoDialogFragment.newInstance(tweet.getVideoUrl());
                        if (diag.getDialog() != null) {
                            diag.getDialog().setCanceledOnTouchOutside(true);
                        }
                        diag.show(fm, "");

                    }
                });

            } else {
                ivPlay.setVisibility(View.GONE);
            }

        }else
        {
            ivTweetMedia.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
        }

        if(tweet.isFavorited())
        {
            tvStarCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_self, 0, 0, 0);
            tvStarCount.setTextColor(Color.parseColor("#FFAC33"));
        }

        if(tweet.isRetweeted())
        {
            tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_self, 0, 0, 0);
            tvRetweetCount.setTextColor(Color.parseColor("#5C913B"));
        }

        tvStarCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Utils.isNetworkAvailable(getApplicationContext()))
                {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (tweet.isFavorited()) {
                        Utils.starUnStarTweet(tweet, false, getApplicationContext());
                        tweet.setFavorited(false);
                        tweet.setFavouritesCount((Integer.parseInt(tweet.getFavouritesCount()) - 1) + "");
                        tvStarCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, 0, 0);
                        tvStarCount.setTextColor(tvDate.getTextColors().getDefaultColor());
                        tvStarCount.setText(tweet.getFavouritesCount());
                    } else {
                        Utils.starUnStarTweet(tweet, true, getApplicationContext());
                        tweet.setFavorited(true);
                        tweet.setFavouritesCount((Integer.parseInt(tweet.getFavouritesCount()) + 1) + "");
                        tvStarCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_self, 0, 0, 0);
                        tvStarCount.setTextColor(Color.parseColor("#FFAC33"));
                        tvStarCount.setText(tweet.getFavouritesCount());
                    }
                }
            }
        });

        tvRetweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isNetworkAvailable(getApplicationContext()))
                {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (tweet.isRetweeted()) {
                        Utils.reUnTweet(tweet, false, getApplicationContext());
                        tweet.setRetweeted(false);
                        tweet.setRetweetCount((Integer.parseInt(tweet.getRetweetCount()) - 1) + "");
                        tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_gray, 0, 0, 0);
                        tvRetweetCount.setTextColor(tvRetweetCount.getTextColors().getDefaultColor());
                        tvRetweetCount.setText(tweet.getRetweetCount());
                    } else {
                        Utils.reUnTweet(tweet, true, getApplicationContext());
                        tweet.setRetweeted(true);
                        tweet.setRetweetCount((Integer.parseInt(tweet.getRetweetCount()) + 1) + "");
                        tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_self, 0, 0, 0);
                        tvRetweetCount.setTextColor(Color.parseColor("#5C913B"));
                        tvRetweetCount.setText(tweet.getRetweetCount());
                    }
                }
            }
        });

        //fetchTweetDetails(tweetId);
    }

    private void fetchTweetDetails(String tweetId)
    {
        client.getTweetDetails(tweetId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                Tweet tweet = Tweet.fromJson(response);

                Toast.makeText(DetailActivity.this, "new ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
        if (id == android.R.id.home) {
            sendBackResult();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Tweet tweet) {
        if(tweet != null){
            reply = tweet;
            Toast.makeText(this, tweet.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendBackResult() {
        Intent data = new Intent();
        data.putExtra("tweet", reply);
        data.putExtra("org", tweet);
        setResult(RESULT_OK, data);
    }
}
