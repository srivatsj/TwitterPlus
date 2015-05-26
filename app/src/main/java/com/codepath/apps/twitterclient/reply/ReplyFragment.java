package com.codepath.apps.twitterclient.reply;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.common.AlertDialogFragment;
import com.codepath.apps.twitterclient.compose.ComposeActivity;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.service.TwitterApplication;
import com.codepath.apps.twitterclient.service.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReplyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReplyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReplyFragment extends DialogFragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TwitterClient client;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView tvReplyTo;
    private EditText etReply;
    private Button btReply;
    private TextView tvCount;
    private TextView tvClose;
    private final static int TWITTER_COUNT_LIMIT = 140;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ReplyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReplyFragment newInstance(Tweet tweet) {
        ReplyFragment fragment = new ReplyFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, tweet);
        fragment.setArguments(args);
        return fragment;
    }

    public ReplyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reply, container);
        final Tweet tweet = (Tweet) getArguments().getParcelable(ARG_PARAM1);
        tvReplyTo = (TextView) view.findViewById(R.id.tvReplyTo);
        tvCount = (TextView) view.findViewById(R.id.tweetCount);
        tvReplyTo.setText("In reply to @" + tweet.getUser().getScreenName());
        etReply = (EditText) view.findViewById(R.id.etReply);
        etReply.setText("@" + tweet.getUser().getScreenName() + " ");
        etReply.requestFocus();
        btReply = (Button) view.findViewById(R.id.btReply);
        tvClose = (TextView)view.findViewById(R.id.tvClose);
        client = TwitterApplication.getRestClient();

        tvCount.setText(TWITTER_COUNT_LIMIT - etReply.getText().toString().length() + "");

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FragmentManager fm = getFragmentManager();
                //AlertDialogFragment alertDialog = AlertDialogFragment.newInstance("Confirm", "Do you want close before replying to tweet?");
                //alertDialog.show(fm, "fragment_alert");
                dismiss();
            }
        });

        btReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = etReply.getText().toString();

                if(!Utils.isNetworkAvailable(getActivity().getApplicationContext()))
                {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
                }
                else {
                    client.postNewTweet(tweet.getUid() + "", status, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("DEBUG", response.toString());
                            Tweet tweet = Tweet.fromJson(response);

                            OnFragmentInteractionListener listener = (OnFragmentInteractionListener) getActivity();
                            listener.onFragmentInteraction(tweet);

                            dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    });

                }
            }

        });


        etReply.addTextChangedListener(new TextWatcher() {
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
                int charleft = 140 - s.toString().length();
                tvCount.setText(charleft + "");
            }
        });

        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Tweet tweet);
    }

}
