package com.codepath.apps.twitterclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.EndlessScrollListener;
import com.codepath.apps.twitterclient.home.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.home.UserArrayAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjayaram on 5/30/2015.
 */
public abstract class UserFragment extends Fragment {

    protected TwitterClient client;
    protected UserArrayAdapter aUsers;
    protected ArrayList<User> users;
    protected ListView lvUsers;
    protected SwipeRefreshLayout swipeContainer;
    protected ProgressBar progressBarFooter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, container, false);
        lvUsers = (ListView)v.findViewById(R.id.lvUsers);

        View footer = inflater.inflate(R.layout.footer_progress, null);
        // Find the progressbar within footer
        progressBarFooter = (ProgressBar)footer.findViewById(R.id.pbFooterLoading);
        // Add footer to ListView before setting adapter
        lvUsers.addFooterView(footer);

        lvUsers.setAdapter(aUsers);
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


        lvUsers.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                populateTimeline();
            }
        });

        return v;
    }

    public void showProgressBar() {
        if(progressBarFooter!=null) {
            progressBarFooter.setVisibility(View.VISIBLE);
        }
    }

    // Hide progress
    public void hideProgressBar() {
        if(progressBarFooter!=null) {
            progressBarFooter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = new ArrayList<>();
        aUsers = new UserArrayAdapter(getActivity(), users, true);
        client = TwitterApplication.getRestClient();
    }

    public void addAll(List<User> tweets){
        aUsers.addAll(tweets);
    }

    //override in subclass
    public abstract void populateTimeline();

    //override in subclass
    public abstract void refreshTimeline();


}
