package com.codepath.apps.twitterclient.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by sjayaram on 5/26/2015.
 */
public class AlertDialogFragment extends DialogFragment {

    DialogResultListener dialogResultListener;

    public AlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    // This interface needs to be implemented by the parent activity
    public interface DialogResultListener {
        // This method will be used to send the result back from the fragment back to the activity
        // "Fragment => Activity"
        public void getResponse(boolean result);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dialogResultListener = (DialogResultListener) activity;
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            dialogResultListener = (DialogResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogResultListener");
        }
    }

    // This method sends the result of the fragment back to the parent activity
    public void sendResult(boolean result) {
        // getRetryRequest() is implemented by the parent activity
        dialogResultListener.getResponse(result);
    }

    // This is used to pass the data from "Activity => Fragment"
    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(true);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(false);
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

}
