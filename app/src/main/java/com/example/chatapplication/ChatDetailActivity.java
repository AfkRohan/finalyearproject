package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatapplication.Adapters.ChatAdapter;
import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final  String senderId = auth.getUid();
        String receivedId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profilePic);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        // Video Call

        binding.videoCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this,VideocallActivity.class);
                Bundle extras= new Bundle();
                startActivity(intent);
            }
        });

        final ArrayList <MessagesModel> messagesModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messagesModels, this , receivedId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receivedId;
        final  String receiverRoom = receivedId + senderId;

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModels.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    MessagesModel model = snapshot1.getValue(MessagesModel.class);
                    model.setMessageId(snapshot1.getKey());

                    messagesModels.add(model);
                }
                chatAdapter.notifyDataSetChanged();
                binding.chatRecyclerView.smoothScrollToPosition(binding.chatRecyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     // attachments
        imgView = findViewById(R.id.attachments4);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachments atch = new attachments();
                atch.show(getSupportFragmentManager(),atch.getTag());

            }
        });
       /*
        //Gallery Icon
        imgView = findViewById(R.id.gallery);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        //Map Icon
        imgView = findViewById(R.id.location);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(intent,2);
                }
            }
        });

        //Camera
         imgView = findViewById(R.id.camera);
         imgView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                     String targetFilename = "/root";
                     Uri locationForPhotos = null;
                     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                     intent.putExtra(MediaStore.EXTRA_OUTPUT,
                             Uri.withAppendedPath(locationForPhotos, targetFilename));
                     if (intent.resolveActivity(getPackageManager()) != null) {
                         startActivityForResult(intent, 3);
                     }
             }
         });

         //documents
         imgView = findViewById(R.id.documents);
         imgView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                 sharingIntent.setType("text/html");
                 sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text shared.</p>"));
                 startActivityForResult(sharingIntent,4);
             }
         });
          */
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etmessage.getText().toString().isEmpty()) {
                    Toast.makeText(ChatDetailActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
                else {
                    String message = binding.etmessage.getText().toString();
                    final MessagesModel model = new MessagesModel(senderId, message);
                    model.setTimestamp(new Date().getTime());
                    binding.etmessage.setText("");
                    binding.chatRecyclerView.smoothScrollToPosition(binding.chatRecyclerView.getAdapter().getItemCount());
                    database.getReference().child("chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
