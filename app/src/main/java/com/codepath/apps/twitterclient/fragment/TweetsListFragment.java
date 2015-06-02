package com.codepath.apps.twitterclient.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.twitterclient.Profile.ProfileActivity;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.EndlessScrollListener;
import com.codepath.apps.twitterclient.common.TweetFragmentObj;
import com.codepath.apps.twitterclient.detail.DetailActivity;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.home.TimelineActivity;
import com.codepath.apps.twitterclient.home.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.reply.ReplyFragment;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjayaram on 5/29/2015.
 */
public abstract class TweetsListFragment extends Fragment{

    protected TwitterClient client;
    protected TweetsArrayAdapter aTweets;
    protected ArrayList<Tweet> tweets;
    protected ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected ProgressBar progressBarFooter;
    private final int DETAIL_REQUEST_CODE = 200;
    protected String maxId;
    protected String sinceId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        lvTweets = (ListView)v.findViewById(R.id.lvTweets);

        View footer = inflater.inflate(R.layout.footer_progress, null);

        // Find the progressbar within footer
        progressBarFooter = (ProgressBar)footer.findViewById(R.id.pbFooterLoading);
        // Add footer to ListView before setting adapter
        lvTweets.addFooterView(footer);

        lvTweets.setAdapter(aTweets);
        swipeContainer = (SwipeRefreshLayout)v.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refreshTimeline();
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

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = (Tweet) parent.getItemAtPosition(position);
                Intent i = new Intent((TimelineActivity)getActivity(), DetailActivity.class);
                i.putExtra("tweet", tweet);
                getActivity().startActivityForResult(i, DETAIL_REQUEST_CODE);
            }
        });

        // Step 4 - Setup the listener for this object
        aTweets.setTweetFragmentObjListener(new TweetFragmentObj.TweetFragmentListerner() {

            @Override
            public void onProfileClick(Tweet data) {

                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra("user", data.getUser());
                getActivity().startActivity(i);

            }

            @Override
            public void onReply(Tweet data) {

                FragmentManager fm = (getActivity()).getFragmentManager();
                ReplyFragment diag = ReplyFragment.newInstance(data);

                if (diag.getDialog() != null) {
                    diag.getDialog().setCanceledOnTouchOutside(true);
                }
                diag.show(fm, "");

            }

            @Override
            public void onFavourite(Tweet tweet) {
                if(!Utils.isNetworkAvailable(getActivity()))
                {
                    Toast.makeText(getActivity(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (tweet.isFavorited()) {
                        Utils.starUnStarTweet(tweet, false, getActivity());
                        tweet.setFavorited(false);
                        tweet.setFavouritesCount((Integer.parseInt(tweet.getFavouritesCount()) - 1) + "");
                    } else {
                        Utils.starUnStarTweet(tweet, true, getActivity());
                        tweet.setFavorited(true);
                        tweet.setFavouritesCount((Integer.parseInt(tweet.getFavouritesCount()) + 1) + "");
                    }

                    aTweets.notifyDataSetChanged();
                }

            }

            @Override
            public void onRetweet(Tweet tweet) {

                if(!Utils.isNetworkAvailable(getActivity()))
                {
                    Toast.makeText(getActivity(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (tweet.isRetweeted()) {
                        Utils.reUnTweet(tweet, false, getActivity());
                        tweet.setRetweeted(false);
                        tweet.setRetweetCount((Integer.parseInt(tweet.getRetweetCount()) - 1) + "");
                    } else {
                        Utils.reUnTweet(tweet, true, getActivity());
                        tweet.setRetweeted(true);
                        tweet.setRetweetCount((Integer.parseInt(tweet.getRetweetCount()) + 1) + "");
                    }

                    aTweets.notifyDataSetChanged();
                }

            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void showProgressBar() {
        if(progressBarFooter!=null) {
            progressBarFooter.setVisibility(View.VISIBLE);
        }
    }

    // Hide progress
    public void hideProgressBar() {
        if(progressBarFooter!=null)
            progressBarFooter.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        client = TwitterApplication.getRestClient();
    }

    public void addAll(List<Tweet> tweets){
        aTweets.addAll(tweets);
    }

    public void insert(Tweet tweet){
        aTweets.insert(tweet, 0);
    }

    public void updateTweet(Tweet tweet2){
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

    //override in subclass
    public abstract void populateTimeline();

    //override in subclass
    public abstract void refreshTimeline();


}
