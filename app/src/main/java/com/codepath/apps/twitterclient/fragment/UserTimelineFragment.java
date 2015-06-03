package com.codepath.apps.twitterclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.detail.DetailActivity;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjayaram on 5/29/2015.
 */
public class UserTimelineFragment extends TweetsListFragment {

    public static UserTimelineFragment newInstance(String screeName){
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screeName);
        userFragment.setArguments(args);
        return userFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateTimeline();
    }

    //@Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container, savedInstanceState);
        return v;
    }

    @Override
    public void populateTimeline()
    {
        String screeName = getArguments().getString("screenName");

        if(!Utils.isNetworkAvailable(getActivity()))
        {
            Toast.makeText(getActivity(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "maxId is " + maxId, Toast.LENGTH_SHORT).show();
            showProgressBar();
            client.getUserTimeline(screeName, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());
                    ArrayList<Tweet> tweetList = Tweet.fromJsonArray(response);
                    maxId = tweetList.get(tweetList.size() - 1).getUid() - 1 + "";
                    addAll(tweetList);
                    hideProgressBar();
                    Toast.makeText(getActivity(), "maxId " + maxId + " Count " + tweetList.size(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    hideProgressBar();
                }
            });
        }
    }

    @Override
    public void refreshTimeline() {

    }
}
