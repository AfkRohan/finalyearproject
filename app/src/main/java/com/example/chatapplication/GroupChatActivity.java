package com.example.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapplication.Adapters.GroupChatAdapter;
import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class GroupChatActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 5 ;
    ActivityGroupChatBinding binding;
    ImageView imgView;
    FirebaseDatabase database;
    String groupName;
    String groupId;
    String groupIcon;
    Users[] user;
    private Uri datafile;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String senderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
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
        database = FirebaseDatabase.getInstance();
        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();

        senderId = FirebaseAuth.getInstance().getUid();
        final String[] senderName = new String[1];
        user = new Users[]{null};
        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        groupIcon = getIntent().getStringExtra("groupIcon");
        groupDesc = getIntent().getStringExtra("groupDesc");
        groupAdminId = getIntent().getStringExtra("groupAdminId");
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
        binding.userName.setText(groupName);
        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_user).into(binding.profilePic);

        final GroupChatAdapter adapter = new GroupChatAdapter(messagesModels,this);
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(linearLayout);

        database.getReference().child("GroupChats").child(groupId).addValueEventListener(new ValueEventListener() {
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
        binding.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGroupProfile();
            }
        });
        binding.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGroupProfile();
            }
        });
        
        imgView = (ImageView)findViewById(R.id.attachments);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
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
                database.getReference().child("GroupChats").child(groupId).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
            }
        });
    }
    String groupDesc;
    String groupAdminId;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_GET && resultCode==RESULT_OK && data!=null){
            datafile = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);
        UploadTask uploadTask;
        uploadTask = riversRef.putFile(datafile);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String imageUrl = downloadUri.toString();
                            final MessagesModel model = new MessagesModel(senderId, user[0].getUserName(), imageUrl);
                            model.setTimestamp(new Date().getTime());
                            model.setType("image");
                            binding.etmessage.setText("");
                            binding.chatRecyclerView.smoothScrollToPosition(binding.chatRecyclerView.getAdapter().getItemCount());
                            database.getReference().child("GroupChats").child(groupId).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Couldn't load Image ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Sorry Message was not sent successfully",Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100* snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                int percent = (int)progressPercentage;
                pd.setMessage("Sending: " + percent + "%");
            }
        });
    }

    private void openGroupProfile() {
        Intent intent = new Intent(this, GroupProfile.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("groupIcon", groupIcon);
        intent.putExtra("groupName", groupName);
        intent.putExtra("groupDesc", groupDesc);
        intent.putExtra("groupAdminId", groupAdminId);
        //intent.putExtra("groupObject", (Serializable) group[0]);
        this.startActivity(intent);
    }
}