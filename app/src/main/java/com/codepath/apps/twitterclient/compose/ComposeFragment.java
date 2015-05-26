package com.codepath.apps.twitterclient.compose;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.reply.ReplyFragment;

/**
 * Created by sjayaram on 5/25/2015.
 */
public class ComposeFragment extends DialogFragment {

    public static ComposeFragment newInstance() {
        ComposeFragment fragment = new ComposeFragment();
        //setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FULLSCREEN_DIALOG);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 250);
        window.setGravity(Gravity.BOTTOM);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose, container);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

/*
        Window window = getDialog().getWindow();

        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.TOP|Gravity.LEFT);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 300;
        params.y = 100;
        window.setAttributes(params);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, 10);
*/

        return view;
    }

}
