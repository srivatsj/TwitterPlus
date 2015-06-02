package com.codepath.apps.twitterclient.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by sjayaram on 5/20/2015.
 */
public class Utils {

    final static String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    private static User loggedInUser;
    private static TwitterClient client;

    public static void starUnStarTweet(final Tweet tweet, final boolean flag, final Context context)
    {
        client = TwitterApplication.getRestClient();
        client.starTweet(tweet.getUid() + "", flag, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());

                if (flag) {
                    Toast.makeText(context, R.string.tweet_favourite, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, R.string.tweet_unfavourite, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public static void reUnTweetView(TextView view, Tweet tweet)
    {
        if(tweet.isRetweeted()){
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_self, 0, 0, 0);
            view.setTextColor(Color.parseColor("#5C913B"));
        }
        else
        {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_gray, 0, 0, 0);
            view.setTextColor(Integer.MIN_VALUE);
        }
        view.setText(countToWords(tweet.getRetweetCount()));

    }

    public static String countToWords(String count){
        int number = Integer.parseInt(count);
        String[] suffix = new String[]{"K","M","B","T"};
        int size = (number != 0) ? (int) Math.log10(number) : 0;
        if (size >= 3){
            while (size % 3 != 0) {
                size = size - 1;
            }
        }
        double notation = Math.pow(10, size);
        String result = (size >= 3) ? + (Math.round((number / notation) * 100) / 100.0d)+suffix[(size/3) - 1] : + number + "";
        return result;
    }

    public static void starTweetView(TextView view, Tweet tweet)
    {
        if(tweet.isFavorited()){
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_self, 0, 0, 0);
            view.setTextColor(Color.parseColor("#FFAC33"));
        }
        else
        {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, 0, 0);
            view.setTextColor(Integer.MIN_VALUE);
        }
        view.setText(countToWords(tweet.getFavouritesCount()));
    }

    public static void reUnTweet(final Tweet tweet, final boolean flag, final Context context)
    {
        client = TwitterApplication.getRestClient();
        String id;
        if (!flag) {
            id = tweet.getCurrent_user_retweet();
        }
        else
        {
            id = tweet.getUid() + "";
        }

        client.reTweet(id, flag, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());

                try {
                    if (flag) {
                        Toast.makeText(context, R.string.tweet_retweet, Toast.LENGTH_SHORT).show();
                         tweet.setCurrent_user_retweet(response.getString("id_str"));
                    } else {
                        Toast.makeText(context, R.string.tweet_untweet, Toast.LENGTH_SHORT).show();
                        tweet.setCurrent_user_retweet("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public static String convertToTime(String rawJsonDate) {

        SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
        sf.setLenient(true);
        String result = new String();

        try
        {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            result = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            result = result.replace(" ago", "");
            result = result.replaceAll("minutes", "m");
            result = result.replaceAll("hours", "h");
            result = result.replaceAll("days", "d");
            result = result.replaceAll("weeks", "w");
            result = result.replaceAll("years", "y");
            result = result.replaceAll("minute", "m");
            result = result.replaceAll("hour", "h");
            result = result.replaceAll("day", "d");
            result = result.replaceAll("week", "w");
            result = result.replaceAll("year", "y");

        } catch (ParseException e) {
            Log.d("Error", e.getMessage());
            return "";
        }

        return result;
    }

    public static void linkifyText(TextView caption){
        Pattern hashTagsPattern = Pattern.compile("(#[a-zA-Z0-9_-]+)");

        //Scheme for Linkify, when a word matched tagMatcher pattern,
        //that word is appended to this URL and used as content URI
        String newActivityURL = "http://twitter.com";
        //Attach Linkify to TextView
        Linkify.addLinks(caption, hashTagsPattern, newActivityURL);

        Pattern pattern = Pattern.compile("(@[A-Za-z0-9_-]+)");
        String scheme = "http://twitter.com/";
        Linkify.addLinks(caption, pattern, scheme, null, mentionFilter);
        caption.setLinkTextColor(Color.parseColor("#2792ff"));
    }

    static Linkify.TransformFilter mentionFilter = new Linkify.TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            Log.i("tagstest", match.group(1));
            return match.group(1);
        }
    };

    public static Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}
