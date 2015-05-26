package com.codepath.apps.twitterclient.compose;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.home.TimelineActivity;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ComposeActivity extends ActionBarActivity {

    private ImageView ivProfile;
    private TextView tvUsername;
    private TextView tvScreename;
    private EditText etTweet;
    private TwitterClient client;
    private User user;
    private MenuItem actionViewItem;
    private final static int TWITTER_COUNT_LIMIT = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = Utils.getLoggedInUser();
        client = TwitterApplication.getRestClient();

        ivProfile = (ImageView)findViewById(R.id.ivProfile);
        tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvScreename = (TextView)findViewById(R.id.tvScreename);
        etTweet = (EditText)findViewById(R.id.etTweet);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        if(user != null) {
            tvUsername.setText(user.getName());
            tvScreename.setText("@" + user.getScreenName());
            ivProfile.setImageResource(android.R.color.transparent);
            Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfile);
        }

        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                //Toast.makeText(ComposeActivity.this, "length " + s.length(), Toast.LENGTH_SHORT).show();
                int charleft = TWITTER_COUNT_LIMIT - s.toString().length();
                actionViewItem.setTitle(charleft + "");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        actionViewItem = menu.findItem(R.id.tweetCount);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            String status = etTweet.getText().toString();

            if(!Utils.isNetworkAvailable(this))
            {
                Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
            }
            else {
                client.postNewTweet(null, status, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", response.toString());
                        Toast.makeText(ComposeActivity.this, "tweet posted ", Toast.LENGTH_SHORT).show();
                        Tweet tweet = Tweet.fromJson(response);
                        Intent data = new Intent();
                        data.putExtra("tweet", tweet);
                        setResult(RESULT_OK, data); // set result code and bundle data for response
                        finish(); // closes the activity, pass data to parent
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                    }
                });

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
