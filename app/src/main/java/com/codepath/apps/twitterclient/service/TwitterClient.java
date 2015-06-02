package com.codepath.apps.twitterclient.service;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "g8LFYTHLxQOfLZ3IymoCjc9IV";       // Change this
	public static final String REST_CONSUMER_SECRET = "4i55VUwzoghSJSEjSOIoLg80rBU9oS5aHiKnG9obwqfId0qOeU"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)
    public static final String RESPONSE_COUNT = "10"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

     public void getHomeTimeline(String maxID, String sinceID, AsyncHttpResponseHandler handler){
         String apiUrl = getApiUrl("statuses/home_timeline.json");

         RequestParams params = new RequestParams();
         params.put("count", 25);
         params.put("include_my_retweet", "true");

         //for endless scroll
         if(!"".equals(maxID) && maxID!=null)
         {
             params.put("max_id",  maxID);
         }

         //for swipe to refresh
         if(!"".equals(sinceID) && sinceID!=null)
         {
             params.put("since_id", sinceID);
         }

         getClient().get(apiUrl, params, handler);
    }

    public void getMentionsline(String maxID, String sinceID, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);

        //for endless scroll
        if(!"".equals(maxID) && maxID!=null)
        {
            params.put("max_id",  maxID);
        }

        //for swipe to refresh
        if(!"".equals(sinceID) && sinceID!=null)
        {
            params.put("since_id", sinceID);
        }

        getClient().get(apiUrl, params, handler);

    }

    public void getLoggedInUserProfile(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");

        RequestParams params = new RequestParams();

        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/user_timeline.json");

        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    public void getUserObj(String screenName, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("users/show.json");

        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    public void getFollowingForUsers(String userId, String cursor, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("friends/list.json");

        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        params.put("cursor", cursor);
        getClient().get(apiUrl, params, handler);
    }

    public void getFollowersForUser(String userId, String cursor, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("followers/list.json");

        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        params.put("cursor", cursor);
        getClient().get(apiUrl, params, handler);
    }

    public void getTweetDetails(String id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("show/" + id + ".json");

        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

    public void postNewTweet(String id, String status, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");

        RequestParams params = new RequestParams();
        if(id != null)
            params.put("in_reply_to_status_id", id);

        params.put("status", status);
        getClient().post(apiUrl, params, handler);
    }

    public void starTweet(String id, boolean flag, AsyncHttpResponseHandler handler){
        String apiUrl;
        if(flag)
            apiUrl = getApiUrl("favorites/create.json");
        else
            apiUrl = getApiUrl("favorites/destroy.json");

        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void reTweet(String id, boolean flag, AsyncHttpResponseHandler handler){
        String apiUrl;

        if(flag)
            apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
        else
            apiUrl = getApiUrl("statuses/destroy/" + id + ".json");


        RequestParams params = new RequestParams();
        getClient().post(apiUrl, params, handler);
    }


}