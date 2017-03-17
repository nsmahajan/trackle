package com.example.neha.trackle;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by neha on 4/3/2016.
 */
public class AboutFragment extends Fragment{
    private ImageButton sendMail;

    /**
     * The function loads the layout file for the fragment.Assigns click events to button in the layout and set defaults values.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return returns the view instance.
     *
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        sendMail = (ImageButton) rootView.findViewById(R.id.sendMail);
        sendMail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
        return rootView;
    }

    /**
     * The function loads an intent, to launch the gmail application in order to send the mails to developers.
     * It pre-populates the subject, cc and body of the mail.
     *
     * @param -
     * @return -
     *
     */
    private void sendMail(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("plain/text");
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "nsmahajan@wpi.edu","avadivel@wpi.edu", "kmohan@wpi.edu" });
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Report Bugs");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello. Please enter your comments below");
        startActivity(sendIntent);
    }
}
