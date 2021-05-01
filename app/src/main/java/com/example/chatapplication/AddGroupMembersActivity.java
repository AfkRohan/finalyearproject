package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Models.Friend;
import com.example.chatapplication.Models.Group;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.databinding.ActivityAddGroupMembersBinding;
import com.example.chatapplication.databinding.ActivityCreateGroupBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddGroupMembersActivity extends AppCompatActivity {
    View FriendView;
    RecyclerView friendList;
    ActivityAddGroupMembersBinding binding;
    DatabaseReference frdRef, usersRef;
    FirebaseAuth auth;
    String groupName;
    String groupId;
    String groupIcon;
    String groupDesc;
    String groupAdminId;
    String currentUserId;
    Context context;
    Group group;
    static ArrayList<String> selectedUsers = new ArrayList<>();
    boolean isSelectMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add_group_members);
        binding = ActivityAddGroupMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        groupIcon = getIntent().getStringExtra("groupIcon");
        groupDesc = getIntent().getStringExtra("groupDesc");
        groupAdminId = getIntent().getStringExtra("groupAdminId");
        context = this;
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        friendList = (RecyclerView) findViewById(R.id.resultList);
        friendList.setLayoutManager(new LinearLayoutManager(this));
        frdRef = FirebaseDatabase.getInstance().getReference().child("UsersFriend").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        selectedUsers.clear();
        FirebaseDatabase.getInstance().getReference().child("GroupsMeta").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        group = snapshot.getValue(Group.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(frdRef, Friend.class).build();
        FirebaseRecyclerAdapter<Friend, AddGroupMembersActivity.FriendViewHolder> adapter = new FirebaseRecyclerAdapter<Friend, AddGroupMembersActivity.FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddGroupMembersActivity.FriendViewHolder holder, int position, @NonNull Friend model) {
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
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
            public AddGroupMembersActivity.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_show_user,parent,false);
                AddGroupMembersActivity.FriendViewHolder viewHolder = new AddGroupMembersActivity.FriendViewHolder(view);
                return viewHolder;
            }
        };
        friendList.setAdapter(adapter);
        adapter.startListening();
        binding.addmembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < selectedUsers.size(); i++) {
                    if(group.getGroupMembers().contains(selectedUsers.get(i))) {
                        selectedUsers.remove(i);
                    }
                }
                ArrayList<String> tempAll = group.getGroupMembers();
                for(int i = 0; i < selectedUsers.size(); i++) {
                    tempAll.add(selectedUsers.get(i));
                }
                FirebaseDatabase.getInstance().getReference().child("GroupsMeta")
                        .child(groupId).child("groupMembers").setValue(tempAll);
                for(int i = 0; i < selectedUsers.size(); i++) {
                    FirebaseDatabase.getInstance().getReference().child("GroupMembers").child(group.getGroupId())
                            .child(selectedUsers.get(i)).child("isMember").setValue("Yes")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }
                for(int i = 0; i < selectedUsers.size(); i++) {
                    FirebaseDatabase.getInstance().getReference().child("UsersGroups")
                            .child(selectedUsers.get(i)).child(group.getGroupId())
                            .child("isAdmin").setValue("No")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(AddGroupMembersActivity.this, "Group Members Added", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddGroupMembersActivity.this, GroupProfile.class);
                                        intent.putExtra("groupId", groupId);
                                        intent.putExtra("groupIcon", groupIcon);
                                        intent.putExtra("groupName", groupName);
                                        intent.putExtra("groupDesc", groupDesc);
                                        intent.putExtra("groupAdminId", groupAdminId);
                                        //intent.putExtra("groupObject", (Serializable) group[0]);
                                        AddGroupMembersActivity.this.startActivity(intent);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });
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