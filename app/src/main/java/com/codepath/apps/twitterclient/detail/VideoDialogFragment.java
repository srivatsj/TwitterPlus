package com.codepath.apps.twitterclient.detail;

/**
 * Created by sjayaram on 5/22/2015.
 */
import android.app.Dialog;
import android.app.DialogFragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.codepath.apps.twitterclient.R;

public class VideoDialogFragment extends DialogFragment {

    public static final String KEY_URL = "url";

    static VideoDialogFragment newInstance(String videoUrl) {
        VideoDialogFragment fragment = new VideoDialogFragment();

        Bundle args = new Bundle();
        args.putString(KEY_URL, videoUrl);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FULLSCREEN_DIALOG);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_video, container, false);

        VideoView videoView = (VideoView) view.findViewById(R.id.vvVideo);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(getArguments().getString(KEY_URL)));
        videoView.setMediaController(new MediaController(getActivity()));
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                VideoDialogFragment.this.dismiss();
            }
        });
        videoView.start();

        return view;
    }
}