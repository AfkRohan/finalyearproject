package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class VideoCallActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall);

        Intent intent = getIntent();
        String senderRoom = intent.getStringExtra("sRoom");
        String receiverRoom = intent.getStringExtra("rRoom");

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