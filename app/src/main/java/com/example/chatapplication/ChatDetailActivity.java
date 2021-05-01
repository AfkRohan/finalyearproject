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

import com.example.chatapplication.Adapters.ChatAdapter;
import com.example.chatapplication.Fragments.APIServices;
import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.Notifications.Client;
import com.example.chatapplication.Notifications.Data;
import com.example.chatapplication.Notifications.MyResponse;
import com.example.chatapplication.Notifications.Sender;
import com.example.chatapplication.Notifications.Token;
import com.example.chatapplication.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.String.format;

public class  ChatDetailActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int REQUEST_IMAGE_GET = 5;
    ActivityChatDetailBinding binding;
    FirebaseUser user;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ImageView imgView;
    public Uri datafile;
    private String imageUrl;
    private String  receiver;
    private String message;
    private DatabaseReference reference;

    boolean notify = false;
    APIServices apiServices;
    private DatabaseReference NotificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        // NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        //apiServices = Client.getClient("https://fcm.google.com/").create(APIServices.class);
        apiServices = Client.getClient("https://fcm.googleapis.com/").create(APIServices.class);

        String currentUser = user.getUid();
        final String[] currentUsername = new String[1];
        //Get User name of currently logged in user.
        database.getReference("Users")
                .child(currentUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUsername[0] = snapshot.child("userName").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        final  String senderId = auth.getUid();
        String receivedId = getIntent().getStringExtra("userId");
        receiver=receivedId;
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_user).into(binding.profilePic);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

//         Video Call
//        binding.videoCallIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String call_time = valueOf(Calendar.getInstance().get(Calendar.HOUR))+":"+ valueOf(Calendar.getInstance().get(Calendar.MINUTE));
//                int year = Calendar.getInstance().get(Calendar.YEAR);
//                int month = Calendar.getInstance().get(Calendar.MONTH);
//                int day = Calendar.getInstance().get(Calendar.DATE);
//                final Video_Call video_call= new Video_Call(currentUsername[0],senderRoom,receiverRoom,call_time,day,month,year);
//                database.getReference()
//                        .child("video_call")
//                        .child(senderRoom)
//                        .push()
//                        .setValue(video_call)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                database.getReference()
//                                        .child("video_call")
//                                        .child(receiverRoom)
//                                        .push()
//                                        .setValue(video_call)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//
//                                            }
//                                        });
//                            }
//                        });
//
//                Intent calling = new Intent(ChatDetailActivity.this,VideoCallActivity.class);
//                calling.putExtra("userName", currentUsername[0]);
//                calling.putExtra("friend", userName);
//                calling.putExtra("sRoom",senderRoom);
//                calling.putExtra("rRoom",receiverRoom);
//                startActivity(calling);
//            }
//        });

        final String senderRoom = senderId + receivedId;
        final  String receiverRoom = receivedId + senderId;
        final ArrayList <MessagesModel> messagesModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messagesModels, this , receivedId,senderId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        //reading chat or setting it to recycle view
        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModels.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    MessagesModel model = snapshot1.getValue(MessagesModel.class);
                    model.setMessageId(snapshot1.getKey());
                    messagesModels.add(model);
                    //model.isNotificationReceived();
                    // if false then notification on and then remove old value from database and assign new value.
                }
                chatAdapter.notifyDataSetChanged();
                binding.chatRecyclerView.smoothScrollToPosition(binding.chatRecyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        // attachments
        imgView = (ImageView)findViewById(R.id.attachments4);
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


        //pushing chat to database
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify=true;
                message =  binding.etmessage.getText().toString();
                if(!message.isEmpty()){
                    final MessagesModel model = new MessagesModel(senderId,message);
                    //final MessageModel model = new MessageModel(SenderId,message,false); For notifications
                    model.setTimestamp(new Date().getTime());
                    binding.etmessage.setText("");
                    database.getReference().child("chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                            /*
                            HashMap<String,String> chatNotificationMap = new HashMap<>();
                            chatNotificationMap.put("sender",senderId);
                            chatNotificationMap.put("Message",message);
                            NotificationRef.child(receivedId).push().setValue(chatNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                  }
                                }
                            });
                            */
                        }
                    });
                }
            }
        });


        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                /* if(notify) {
                    sendNotification(receiver, user.getUserName(), msg);
                }  */
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver, String userName, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(user.getUid(), userName + ": " + msg, "New Message",FirebaseAuth.getInstance().getCurrentUser().getUid(), R.mipmap.ic_launcher);
                    Sender sender = new Sender(data, token.getToken());

                    apiServices.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(ChatDetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
                            final  String senderId = auth.getUid();
                            final String senderRoom = format("%s%s", senderId, receiver);
                            final String receiverRoom = format("%s%s", receiver, senderId);
                            Uri downloadUri = task.getResult();
                            imageUrl = downloadUri.toString();
                            final MessagesModel model = new MessagesModel(senderId,imageUrl);
                            model.setTimestamp(new Date().getTime());
                            model.setType("image");
                            model.setFirst("true");
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

}