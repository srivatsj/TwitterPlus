package com.codepath.apps.twitterclient.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.fragment.TweetsListFragment;
import com.codepath.apps.twitterclient.fragment.UserTimelineFragment;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sjayaram on 6/2/2015.
 */
public class SearchTweetFragment extends TweetsListFragment {

    public static SearchTweetFragment newInstance(String query){
        SearchTweetFragment userFragment = new SearchTweetFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
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
    public void populateTimeline() {

        Toast.makeText(getActivity(), "maxId is " + maxId, Toast.LENGTH_SHORT).show();

        /*String query = getArguments().getString("query");

        if(!Utils.isNetworkAvailable(getActivity()))
        {
            Toast.makeText(getActivity(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "maxId is " + maxId, Toast.LENGTH_SHORT).show();
            showProgressBar();
            client.searchTwitter(query, maxId, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    ArrayList<Tweet> tweetList;

                    try {
                        tweetList = Tweet.fromJsonArray(response.getJSONArray("statuses"));
                        if (tweetList.size() > 0) {

                            maxId = tweetList.get(tweetList.size() - 1).getUid() - 1 + "";
                            addAll(tweetList);

                            if (sinceId == null)
                                sinceId = aTweets.getItem(0).getUid() + "";

                            Toast.makeText(getActivity(), "maxId " + maxId + " Count " + tweetList.size(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    hideProgressBar();
                }
            });
        }
*/
    }

    @Override
    public void refreshTimeline() {

        String query = getArguments().getString("query");

        if(!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        }
        else {
            sinceId = "1";
            client.searchTwitter(query, null, sinceId, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    ArrayList<Tweet> tweetList;
                    try {
                        tweetList = Tweet.fromJsonArray(response.getJSONArray("statuses"));
                        if (tweetList.size() > 0) {
                            sinceId = tweetList.get(0).getUid() + "";
                            //maxId = tweetList.get(tweetList.size() - 1).getUid() - 1 + "";
                            aTweets.clear();
                            aTweets.addAll(tweetList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeContainer.setRefreshing(false);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    swipeContainer.setRefreshing(false);
                }
            });
        }

    }
}
