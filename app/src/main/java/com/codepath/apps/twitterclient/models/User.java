package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sjayaram on 5/19/2015.
 */
@Table(name = "User")
public class User extends Model implements Parcelable {

    @Column(name = "name")
    private String name;
    @Column(name = "userId", unique = true)
    private long uid;
    @Column(name = "screenName")
    private String screenName;
    @Column(name = "profileImageUrl")
    private String profileImageUrl;

    @Column(name = "followersCount")
    private String followersCount;
    @Column(name = "followingCount")
    private String followingCount;
    @Column(name = "tagLine")
    private String tagLine;
    @Column(name = "statuses_count")
    private String statuses_count;

    @Column(name = "profileBackgroundImg")
    private String profileBackgroundImg;

    //@Column(name = "currentUserFlag")
    //private String profileImageUrl;

    public User() {
        super();
    }

    public static User fromJson(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");

            user.tagLine = jsonObject.getString("description");
            user.followersCount = jsonObject.optString("followers_count")!=null ? jsonObject.getString("followers_count") : "0";
            user.followingCount = jsonObject.optString("friends_count")!=null ? jsonObject.getString("friends_count") : "0";
            user.statuses_count = jsonObject.optString("statuses_count")!=null ? jsonObject.getString("statuses_count") : "0";

            user.profileBackgroundImg = jsonObject.getString("profile_background_image_url");

            User existingUser =
                    new Select().from(User.class).where("userId = ?", user.uid).executeSingle();
            if (existingUser != null) {
                // found and return existing
                return existingUser;
            } else {
                // create and return new user
                user.save();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static ArrayList<User> fromJsonArray(JSONArray jsonArray){
        ArrayList<User> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++)
        {
            try {
                JSONObject tweetjson = jsonArray.getJSONObject(i);
                User tweet = User.fromJson(tweetjson);
                if(tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    public String getProfileBackgroundImg() {
        return profileBackgroundImg;
    }

    public String getStatuses_count() {
        return statuses_count;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUid() {
        return uid;
    }

    public static void deleteAll(){
        new Delete().from(User.class).execute(); // all records
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.followersCount);
        dest.writeString(this.followingCount);
        dest.writeString(this.tagLine);
        dest.writeString(this.statuses_count);
        dest.writeString(this.profileBackgroundImg);
    }

    private User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.followersCount = in.readString();
        this.followingCount = in.readString();
        this.tagLine = in.readString();
        this.statuses_count = in.readString();
        this.profileBackgroundImg = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
