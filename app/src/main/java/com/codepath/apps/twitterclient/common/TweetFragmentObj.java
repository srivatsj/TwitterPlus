package com.codepath.apps.twitterclient.common;

import com.codepath.apps.twitterclient.models.Tweet;

/**
 * Created by sjayaram on 5/31/2015.
 */
public class TweetFragmentObj {

    public interface TweetFragmentListerner {

        public void onProfileClick(Tweet data);

        public void onReply(Tweet data);

        public void onFavourite(Tweet data);

        public void onRetweet(Tweet data);
    }

}
