package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapplication.Adapters.ChatAdapter;
import com.example.chatapplication.Adapters.GroupChatAdapter;
import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();
        final String[] senderName = new String[1];
        final Users[] user = {null};
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user[0] = snapshot.getValue(Users.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.userName.setText("Friends Group");

        final GroupChatAdapter adapter = new GroupChatAdapter(messagesModels,this);
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(linearLayout);

        database.getReference().child("GroupChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModels.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                   MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                   messagesModels.add(model);
                }
                adapter.notifyDataSetChanged();
                binding.chatRecyclerView.smoothScrollToPosition(binding.chatRecyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imgView = findViewById(R.id.attachments);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachments atch = new attachments();
                atch.show(getSupportFragmentManager(),atch.getTag());

            }
        });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etmessage.getText().toString().isEmpty()) {
                    Toast.makeText(GroupChatActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
                else {
                final String message = binding.etmessage.getText().toString();
                final MessagesModel model = new MessagesModel(senderId, user[0].getUserName(), message);
                model.setTimestamp(new Date().getTime());
                binding.etmessage.setText("");
                binding.chatRecyclerView.smoothScrollToPosition(binding.chatRecyclerView.getAdapter().getItemCount());
                database.getReference().child("GroupChats").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
            }
        });
    }
}