package com.codepath.apps.twitterclient.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.LinkifiedTextView;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by sjayaram on 5/30/2015.
 */
public class UserArrayAdapter extends ArrayAdapter<User> {

    private static class ViewHolder {
        ImageView ivProfile;
        TextView tvUsername;
        TextView tvScreename;
        TextView tvTag;
        TextView imageView;
    }

    private TwitterClient client;
    private boolean isFollowType;

    public UserArrayAdapter(Context context, List<User> tweets, boolean flag) {
        super(context, android.R.layout.simple_list_item_1, tweets);
        this.isFollowType = flag;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvScreename = (TextView) convertView.findViewById(R.id.tvScreename);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            viewHolder.tvTag = (TextView) convertView.findViewById(R.id.tvTag);
            viewHolder.imageView = (TextView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUsername.setText(user.getName());
        viewHolder.tvScreename.setText("@" + user.getScreenName());

        viewHolder.ivProfile.setImageResource(android.R.color.transparent);

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(35)
                .oval(true)
                .build();
        Picasso.with(getContext()).load(user.getProfileImageUrl()).transform(transformation).into(viewHolder.ivProfile);
        viewHolder.tvTag.setText(user.getTagLine());

        if(isFollowType){
            viewHolder.imageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.follow, 0, 0, 0);
        }
        else
        {
            viewHolder.imageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unfollow, 0, 0, 0);
        }

        return convertView;
    }

}