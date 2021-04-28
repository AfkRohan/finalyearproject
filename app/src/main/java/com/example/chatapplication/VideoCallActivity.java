package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        String caller = intent.getStringExtra("userName");
        String receiver = intent.getStringExtra("friend");

        String senderRoom = intent.getStringExtra("senderRoom");


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

       // if( senderRoom.length() > 0 ) {
            JitsiMeetConferenceOptions options =
                    new JitsiMeetConferenceOptions.Builder()
                            .setRoom("Video Call Rome")
                            .build();
            JitsiMeetActivity.launch(this, options);
        //}
    }


}