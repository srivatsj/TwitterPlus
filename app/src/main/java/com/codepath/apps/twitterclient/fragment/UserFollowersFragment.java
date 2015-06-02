package com.codepath.apps.twitterclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjayaram on 5/30/2015.
 */
public class UserFollowersFragment extends UserFragment {

    private String cursor = "-1";

    public static UserFollowersFragment newInstance(String userId){
        UserFollowersFragment userFragment = new UserFollowersFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        userFragment.setArguments(args);
        return userFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateTimeline();
    }

    @Override
    public void populateTimeline() {

        String userId = getArguments().getString("userId");

        if(!Utils.isNetworkAvailable(getActivity()))
        {
            Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_SHORT).show();

        }
        else if(!cursor.equals("0")){
            showProgressBar();
            client.getFollowersForUser(userId, cursor, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());

                    ArrayList<User> tweetList = null;
                    try {
                        tweetList = User.fromJsonArray(response.getJSONArray("users"));
                        cursor = response.getString("next_cursor");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addAll(tweetList);
                    hideProgressBar();
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
