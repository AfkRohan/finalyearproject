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

import com.example.chatapplication.Models.Video_Call;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class VideoCallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall);

        getSupportActionBar().hide();
        Intent intent = getIntent();
        String room = intent.getStringExtra("room");

        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(""))
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setup_video_call(room);

    }

    public void setup_video_call(String room){

        if( !room.isEmpty() ) {
            JitsiMeetConferenceOptions options =
                    new JitsiMeetConferenceOptions.Builder()
                            .setRoom(room)
                            .build();
            JitsiMeetActivity.launch(this, options);
        }
    }


}
//Incoming Video Call
//     database.getReference()
//             .child("video_call")
//                .child(senderRoom)
//                .addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            // get time from database and compare with range of time
//            for(DataSnapshot snapshot1 : snapshot.getChildren()){
//                Video_Call video_call = snapshot1.getValue(Video_Call.class);
//                int year = Calendar.getInstance().get(Calendar.YEAR);
//                int month = Calendar.getInstance().get(Calendar.MONTH);
//                int day = Calendar.getInstance().get(Calendar.DATE);
//                if ( video_call.getYear() == year && video_call.getMonth() == month && video_call.getDay() == day ) {
//                    // Today's Call
//                    String call_time = video_call.getCall_time();
//                    if ( video_call.compareDates(call_time) ){
//                        Intent incoming = new Intent(ChatDetailActivity.this,IncomingCall.class);
//                        incoming.putExtra("sRoom",video_call.getsRoom());
//                        incoming.putExtra("rRoom",video_call.getrRoom());
//                        incoming.putExtra("caller",video_call.getUserName());
//                        startActivity(incoming);
//                    }
//                } else {
//                    // earlier calls
//                    //Add to miss call list.
//                }
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    });