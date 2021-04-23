package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.BoringLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatapplication.Adapters.ChatAdapter;
import com.example.chatapplication.Fragments.APIService;
import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.Models.Video_Call;
import com.example.chatapplication.databinding.ActivityChatDetailBinding;
import com.example.chatapplication.notification.Client;
import com.example.chatapplication.notification.Data;
import com.example.chatapplication.notification.MyResponse;
import com.example.chatapplication.notification.Sender;
import com.example.chatapplication.notification.Token;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.String.*;

public class  ChatDetailActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int REQUEST_IMAGE_GET = 5;
    ActivityChatDetailBinding binding;
    FirebaseUser user;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ImageView imgView;
    private static String inputFormat = "HH:mm";
    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.getDefault());
    Calendar calendar = Calendar.getInstance();
    public Uri datafile;
    private String imageUrl;
    private String  receiver;
    APIService apiService;
    private String message;
    private DatabaseReference reference;
    private Boolean notify = false;

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
        String currentUser = user.getUid();

       apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profilePic);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        // Video Call
        final String senderRoom = senderId + receivedId;
        final  String receiverRoom = receivedId + senderId;
        binding.videoCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String call_time = valueOf(Calendar.getInstance().get(Calendar.HOUR))+":"+ valueOf(Calendar.getInstance().get(Calendar.MINUTE));
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int day = Calendar.getInstance().get(Calendar.DATE);
                final Video_Call video_call= new Video_Call(currentUsername[0],senderRoom,receiverRoom,call_time,day,month,year);
                database.getReference()
                        .child("video_call")
                        .child(senderRoom)
                        .push()
                        .setValue(video_call)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference()
                                        .child("video_call")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(video_call)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                            }
                        });

                Intent calling = new Intent(ChatDetailActivity.this,VideoCallActivity.class);
                calling.putExtra("userName", currentUsername[0]);
                calling.putExtra("friend", userName);
                calling.putExtra("sRoom",senderRoom);
                calling.putExtra("rRoom",receiverRoom);
                startActivity(calling);
            }
        });


        //Incoming Video Call
//        database.getReference()
//                .child("video_call")
//                .child(senderRoom)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        // get time from database and compare with range of time
//                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
//                            Video_Call video_call = snapshot1.getValue(Video_Call.class);
//                            int year = Calendar.getInstance().get(Calendar.YEAR);
//                            int month = Calendar.getInstance().get(Calendar.MONTH);
//                            int day = Calendar.getInstance().get(Calendar.DATE);
//                            if ( video_call.getYear() == year && video_call.getMonth() == month && video_call.getDay() == day ) {
//                                // Today's Call
//                                String call_time = video_call.getCall_time();
//                                if ( video_call.compareDates(call_time) ){
//                                    Intent incoming = new Intent(ChatDetailActivity.this,IncomingCall.class);
//                                    incoming.putExtra("sRoom",video_call.getsRoom());
//                                    incoming.putExtra("rRoom",video_call.getrRoom());
//                                    incoming.putExtra("caller",video_call.getUserName());
//                                    startActivity(incoming);
//                                }
//                            } else {
//                                // earlier calls
//                                //Add to miss call list.
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        final ArrayList <MessagesModel> messagesModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messagesModels, this , receivedId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModels.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    MessagesModel model = snapshot1.getValue(MessagesModel.class);
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

       binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify=true;
                message =  binding.etmessage.getText().toString();
                final MessagesModel model = new MessagesModel(senderId,message);
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
                    }
                });
            }
        });


        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if (notify) {
                    sendNotification(receiver, user.getUserName(), msg);
                }
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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Token token = snapshot1.getValue(Token.class);
                    Data data = new Data(user.getUid(),"new Message",userName+ ": " + message," ", R.mipmap.ic_launcher);
                    Sender  sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success==1){
                                            Toast.makeText(ChatDetailActivity.this,"failed",Toast.LENGTH_SHORT).show();
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
