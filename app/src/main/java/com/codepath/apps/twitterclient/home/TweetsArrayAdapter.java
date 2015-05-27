package com.codepath.apps.twitterclient.home;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
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

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.LinkifiedTextView;
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

    private TwitterClient client;

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


        if(tweet.isFavorited())
        {
            viewHolder.tvStarCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_self, 0, 0, 0);
            viewHolder.tvStarCount.setTextColor(Color.parseColor("#FFAC33"));
        }
        else
        {
            viewHolder.tvStarCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, 0, 0);
            viewHolder.tvStarCount.setTextColor(NO_SELECTION);
        }

        if(tweet.isRetweeted())
        {
            viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_self, 0, 0, 0);
            viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#5C913B"));
        }
        else
        {
            viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_gray, 0, 0, 0);
            viewHolder.tvRetweetCount.setTextColor(NO_SELECTION);
        }


        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvBody.setAutoLinkMask(0);
        Utils.linkifyText(viewHolder.tvBody);
        viewHolder.tvBody.setAutoLinkMask(1);

        viewHolder.tvDate.setText(Utils.convertToTime(tweet.getCreatedAt()));
        viewHolder.tvRetweetCount.setText(tweet.getRetweetCount());//Long.valueOf(tweet.getUid()).toString()
        viewHolder.tvStarCount.setText(tweet.getFavouritesCount());

        viewHolder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((Activity) getContext()).getFragmentManager();
                ReplyFragment diag = ReplyFragment.newInstance(tweet);

                if (diag.getDialog() != null) {
                    diag.getDialog().setCanceledOnTouchOutside(true);
                }
                diag.show(fm, "");

            }
        });

        viewHolder.tvRetweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isNetworkAvailable(getContext()))
                {
                    Toast.makeText(getContext(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (tweet.isRetweeted()) {
                        Utils.reUnTweet(tweet, false, getContext());
                        Tweet tweet = getItem(position);
                        tweet.setRetweeted(false);
                        tweet.setRetweetCount((Integer.parseInt(tweet.getRetweetCount()) - 1) + "");
                        viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_gray, 0, 0, 0);
                        viewHolder.tvRetweetCount.setTextColor(NO_SELECTION);
                        viewHolder.tvRetweetCount.setText(tweet.getRetweetCount());
                    } else {
                        Utils.reUnTweet(tweet, true, getContext());
                        Tweet tweet = getItem(position);
                        tweet.setRetweeted(true);
                        tweet.setRetweetCount((Integer.parseInt(tweet.getRetweetCount()) + 1) + "");
                        viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_self, 0, 0, 0);
                        viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#5C913B"));
                        viewHolder.tvRetweetCount.setText(tweet.getRetweetCount());
                    }
                }
            }
        });


        viewHolder.tvStarCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isNetworkAvailable(getContext()))
                {
                    Toast.makeText(getContext(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (tweet.isFavorited()) {
                        Utils.starUnStarTweet(tweet, false, getContext());
                        Tweet tweet = getItem(position);
                        tweet.setFavorited(false);
                        tweet.setFavouritesCount((Integer.parseInt(tweet.getFavouritesCount()) - 1) + "");
                        viewHolder.tvStarCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, 0, 0);
                        viewHolder.tvStarCount.setTextColor(NO_SELECTION);
                        viewHolder.tvStarCount.setText(tweet.getFavouritesCount());
                    } else {
                        Utils.starUnStarTweet(tweet, true, getContext());
                        Tweet tweet = getItem(position);
                        tweet.setFavorited(true);
                        tweet.setFavouritesCount((Integer.parseInt(tweet.getFavouritesCount()) + 1) + "");
                        viewHolder.tvStarCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_self, 0, 0, 0);
                        viewHolder.tvStarCount.setTextColor(Color.parseColor("#FFAC33"));
                        viewHolder.tvStarCount.setText(tweet.getFavouritesCount());
                    }
                }
            }
        });

        return convertView;
    }



}
