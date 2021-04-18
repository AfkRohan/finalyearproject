package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class IncomingCall extends AppCompatActivity {

    Button accept,decline;
    TextView caller_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        Intent intent = getIntent();
        String senderRoom = intent.getStringExtra("sRoom");
        String receiverRoom = intent.getStringExtra("rRoom");
        String caller = intent.getStringExtra("caller");


        caller_name = (TextView) findViewById(R.id.callerName);
        accept = (Button) findViewById(R.id.btnAnswer);
        decline = (Button) findViewById(R.id.btnDecline);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(new URL(""))
                            .setWelcomePageEnabled(false)
                            .build();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                setup_video_call(senderRoom);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void setup_video_call(String senderRoom){

        if( senderRoom.length() > 0 ) {
            JitsiMeetConferenceOptions options =
                    new JitsiMeetConferenceOptions.Builder()
                            .setRoom(senderRoom)
                            .build();
            JitsiMeetActivity.launch(this, options);
        }
    }

}