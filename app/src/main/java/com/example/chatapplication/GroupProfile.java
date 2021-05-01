package com.example.chatapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Models.Friend;
import com.example.chatapplication.Models.Group;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.databinding.ActivityGroupProfileBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupProfile extends AppCompatActivity {
    ActivityGroupProfileBinding binding;
    private TextView mTextView;
    String groupName;
    String groupId;
    String groupIcon;
    String groupDesc;
    String groupAdminId;
    String currentUserId;
    ArrayList<String> groupMembers;
    private DatabaseReference memRef;
    private DatabaseReference usersRef;
    RecyclerView memberList;
    Context context;
    Toolbar toolbar;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //getSupportActionBar().hide();
        memberList = (RecyclerView) findViewById(R.id.chatRecyclerView);
        memberList.setLayoutManager(new LinearLayoutManager(this));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        groupIcon = getIntent().getStringExtra("groupIcon");
        groupDesc = getIntent().getStringExtra("groupDesc");
        groupAdminId = getIntent().getStringExtra("groupAdminId");
        context = this;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        memRef = FirebaseDatabase.getInstance().getReference().child("GroupMembers").child(groupId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        binding.groupname.setText(groupName);
        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_user).into(binding.groupicon);
        binding.desc.setText(groupDesc);
        FirebaseDatabase.getInstance().getReference().child("GroupsMeta").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        group = snapshot.getValue(Group.class);
                        groupMembers = group.getGroupMembers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(memRef, Friend.class).build();

        FirebaseRecyclerAdapter<Friend, GroupProfile.FriendViewHolder> adapter = new FirebaseRecyclerAdapter<Friend, GroupProfile.FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupProfile.FriendViewHolder holder, int position, @NonNull Friend model) {
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
               holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(currentUserId.equals(groupAdminId)) {
                            holder.itemView.setBackgroundColor(Color.CYAN);
                            new AlertDialog.Builder(context).setTitle("Remove").setMessage("Press YES to remove this member from group...")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(currentUserId.equals(users[0].getUserId())) {
                                                Toast.makeText(GroupProfile.this, "Group Admin can't leave currently", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                FirebaseDatabase.getInstance().getReference().child("UsersGroups")
                                                        .child(users[0].getUserId()).child(groupId).setValue(null);
                                                FirebaseDatabase.getInstance().getReference().child("GroupMembers")
                                                        .child(groupId).child(users[0].getUserId()).setValue(null);
                                                groupMembers.remove(users[0].getUserId());
                                                FirebaseDatabase.getInstance().getReference().child("GroupsMeta")
                                                        .child(groupId).child("groupMembers").setValue(groupMembers);
                                                group.setGroupMembers(groupMembers);
                                            }
                                        }
                                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();;
                                }
                            }).show();
                        }
                        return true;
                    }
                });
            }

            @NonNull
            @Override
            public GroupProfile.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_show_user,parent,false);
                GroupProfile.FriendViewHolder viewHolder = new GroupProfile.FriendViewHolder(view);
                return viewHolder;
            }
        };
        memberList.setAdapter(adapter);
        adapter.startListening();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupProfile.this, GroupChatActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupIcon", groupIcon);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupDesc", groupDesc);
                intent.putExtra("groupAdminId", groupAdminId);
                //intent.putExtra("groupObject", (Serializable) group[0]);
                GroupProfile.this.startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.groupmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addMember:
                Intent intent = new Intent(GroupProfile.this, AddGroupMembersActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupIcon", groupIcon);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupDesc", groupDesc);
                intent.putExtra("groupAdminId", groupAdminId);
                //intent.putExtra("groupObject", (Serializable) group[0]);
                GroupProfile.this.startActivity(intent);
                break;
            case R.id.leaveGroup:
                if(currentUserId.equals(groupAdminId)) {
                    Toast.makeText(GroupProfile.this, "Group Admin can't leave currently", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("UsersGroups")
                            .child(currentUserId).child(groupId).setValue(null);
                    FirebaseDatabase.getInstance().getReference().child("GroupMembers")
                            .child(groupId).child(currentUserId).setValue(null);
                    groupMembers.remove(currentUserId);
                    FirebaseDatabase.getInstance().getReference().child("GroupsMeta")
                            .child(groupId).child("groupMembers").setValue(groupMembers);
                    group.setGroupMembers(groupMembers);
                    Intent intent4 = new Intent(GroupProfile.this, MainActivity.class);
                    startActivity(intent4);
                }
                break;
        }
        return true;
    }

}