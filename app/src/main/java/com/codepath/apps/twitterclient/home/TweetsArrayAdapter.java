package com.codepath.apps.twitterclient.home;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.codepath.apps.twitterclient.Profile.ProfileActivity;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.LinkifiedTextView;
import com.codepath.apps.twitterclient.common.TweetFragmentObj;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.reply.ReplyFragment;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjayaram on 5/19/2015.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private static class ViewHolder {
        ImageView ivProfile;
        TextView tvUsername;
        TextView tvScreename;
        LinkifiedTextView tvBody;
        TextView tvDate;
        TextView tvRetweetCount;
        TextView tvStarCount;
        TextView tvRetweetUser;
        TextView tvReply;
    }

    private TweetFragmentObj.TweetFragmentListerner listener;

    // Assign the listener implementing events interface that will receive the events
    public void setTweetFragmentObjListener(TweetFragmentObj.TweetFragmentListerner listener) {
        this.listener = listener;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        final ViewHolder viewHolder;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvScreename = (TextView) convertView.findViewById(R.id.tvScreename);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvBody = (LinkifiedTextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.tvStarCount = (TextView) convertView.findViewById(R.id.tvStarCount);
            viewHolder.tvRetweetUser = (TextView) convertView.findViewById(R.id.tvRetweetUser);
            viewHolder.tvReply = (TextView)convertView.findViewById(R.id.tvReplyImg);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivProfile.setImageResource(android.R.color.transparent);

        User user =  tweet.getUser();

        if(tweet.getReplied()!=null || tweet.getRetweetUser() != null)
        {
            if(tweet.getReplied()!=null) {
                viewHolder.tvRetweetUser.setVisibility(View.VISIBLE);
                viewHolder.tvRetweetUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.replied, 0, 0, 0);
                viewHolder.tvRetweetUser.setText("In reply to " + tweet.getReplied());
            }
            else{
                viewHolder.tvRetweetUser.setVisibility(View.VISIBLE);
                viewHolder.tvRetweetUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweeted, 0, 0, 0);
                viewHolder.tvRetweetUser.setText(" " + tweet.getUser().getName() + " retweeted");
                user = tweet.getRetweetUser();
            }
        }
        else
        {
            viewHolder.tvRetweetUser.setVisibility(View.GONE);
        }

        viewHolder.tvUsername.setText(user.getName());
        viewHolder.tvScreename.setText("@" + user.getScreenName());
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(35)
                .oval(true)
                .build();
        Picasso.with(getContext()).load(user.getProfileImageUrl()).transform(transformation).into(viewHolder.ivProfile);

        Utils.starTweetView(viewHolder.tvStarCount, tweet);
        Utils.reUnTweetView(viewHolder.tvRetweetCount, tweet);

        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvBody.setAutoLinkMask(0);
        Utils.linkifyText(viewHolder.tvBody);
        viewHolder.tvBody.setAutoLinkMask(1);
        viewHolder.tvDate.setText(Utils.convertToTime(tweet.getCreatedAt()));

        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProfileClick(tweet);
            }
        });

        viewHolder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReply(tweet);
            }
        });

        viewHolder.tvRetweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRetweet(tweet);
            }
        });

        viewHolder.tvStarCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFavourite(tweet);
            }
        });

        return convertView;
    }



}
