package com.example.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Models.Friend;
import com.example.chatapplication.Models.Group;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.databinding.ActivityCreateGroupBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CreateGroupActivity extends AppCompatActivity {
    View FriendView;
    RecyclerView friendList;
    ActivityCreateGroupBinding binding;
    static ArrayList<Users> list = new ArrayList<>();
    static ArrayList<String> selectedUsers = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference frdRef, usersRef;
    FirebaseAuth auth;
    FirebaseStorage storage;
    String currentUserId;
    ProgressDialog progressDialog;
    Uri sFile;
    boolean isSelectMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_create_group);
        setContentView(binding.getRoot());
        friendList = (RecyclerView) findViewById(R.id.chatRecyclerView);
        friendList.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        frdRef = FirebaseDatabase.getInstance().getReference().child("UsersFriend").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog = new ProgressDialog(CreateGroupActivity.this);
        progressDialog.setTitle("Wait a sec...");
        progressDialog.setMessage("Creating new group");
        selectedUsers.clear();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(frdRef, Friend.class).build();

        FirebaseRecyclerAdapter<Friend, CreateGroupActivity.FriendViewHolder> adapter = new FirebaseRecyclerAdapter<Friend, CreateGroupActivity.FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull Friend model) {
                String usersIds = getRef(position).getKey();
                final Users[] users = {new Users()};

                usersRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users[0] = snapshot.getValue(Users.class);
                        /*String profilePic = snapshot.child("profilepic").getValue().toString();
                        String username = snapshot.child("userName").getValue().toString();
*/
                        holder.userName.setText(users[0].getUserName());
                        Picasso.get().load(users[0].getProfilepic()).placeholder(R.drawable.ic_user).into(holder.image);
                        FirebaseDatabase.getInstance().getReference().child("chats").child(currentUserId+users[0].getUserId()).orderByChild("timestamp")
                                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChildren()){
                                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                                        if((snapshot1.child("type").getValue().toString()).equals("text"))
                                            holder.lastMessage.setText(snapshot1.child("message").getValue(String.class));
                                        else
                                            holder.lastMessage.setText("image");
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
               /* holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        isSelectMode = true;
                        if(selectedUsers.contains(users[0])) {
                            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                            selectedUsers.remove(users[0]);
                        }
                        else {
                            holder.itemView.setBackgroundColor(Color.CYAN);
                            selectedUsers.add(users[0]);
                        }
                        if(selectedUsers.size() == 0)
                            isSelectMode = false;
                        return true;
                    }
                });*/
                holder.itemView.setOnClickListener((view) -> {
                    if(true) {
                        if(selectedUsers.contains(users[0].getUserId())) {
                            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                            selectedUsers.remove(users[0].getUserId());
                        }
                        else {
                            holder.itemView.setBackgroundColor(Color.CYAN);
                            selectedUsers.add(users[0].getUserId());
                        }
                        if(selectedUsers.size() == 0)
                            isSelectMode = false;
                    }
                });
            }

            @NonNull
            @Override
            public CreateGroupActivity.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_show_user,parent,false);
                CreateGroupActivity.FriendViewHolder viewHolder = new CreateGroupActivity.FriendViewHolder(view);
                return viewHolder;
            }
        };
        friendList.setAdapter(adapter);
        adapter.startListening();

        binding.creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.txgroupname.getText().toString().isEmpty())
                    Toast.makeText(CreateGroupActivity.this, "Group Name cant be empty", Toast.LENGTH_SHORT).show();
                else {
                    progressDialog.show();
                    String gname = binding.txgroupname.getText().toString();
                    selectedUsers.add(currentUserId);
                    Group newGroup = new Group();
                    newGroup.setGroupAdminId(currentUserId);
                    newGroup.setGroupName(gname);
                    newGroup.setGroupDesc(binding.txgroupdesc.getText().toString());
                    newGroup.setCreatedAt(new Date().getTime());
                    newGroup.setGroupMembers(selectedUsers);
                    DatabaseReference groupMetaRef = FirebaseDatabase.getInstance().getReference().child("GroupsMeta").push();
                    String gId = groupMetaRef.getKey();
                    newGroup.setGroupId(gId);
                    if(sFile != null) {
                        final StorageReference reference = storage.getReference().child("group_pictures")
                                .child(gId);
                        reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseDatabase.getInstance().getReference().child("GroupsMeta").child(gId)
                                                .child("groupIcon").setValue(uri.toString());

                                    }
                                });
                            }
                        });
                    }
                    groupMetaRef.setValue(newGroup).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                    for(int i = 0; i < selectedUsers.size(); i++) {
                        FirebaseDatabase.getInstance().getReference().child("GroupMembers").child(gId)
                                .child(selectedUsers.get(i)).child("isMember").setValue("Yes")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                    selectedUsers.remove(currentUserId);
                    FirebaseDatabase.getInstance().getReference().child("UsersGroups")
                            .child(currentUserId).child(gId)
                            .child("isAdmin").setValue("Yes")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        for(int i = 0; i < selectedUsers.size(); i++) {
                                            FirebaseDatabase.getInstance().getReference().child("UsersGroups")
                                                    .child(selectedUsers.get(i)).child(gId)
                                                    .child("isAdmin").setValue("No")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(CreateGroupActivity.this, MainActivity.class);
                                                                progressDialog.dismiss();
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            }
        });
        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            sFile = data.getData();
            binding.groupicon.setImageURI(sFile);
        }
    }


    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, lastMessage;

    public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }

}