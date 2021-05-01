package com.example.chatapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.GroupChatActivity;
import com.example.chatapplication.Models.Group;
import com.example.chatapplication.Notifications.Token;
import com.example.chatapplication.R;
import com.example.chatapplication.databinding.FragmentGroupBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    FragmentGroupBinding binding;
    FirebaseDatabase database;
    View GroupView;
    RecyclerView groupList;
    DatabaseReference grpRef, groupsMetsRef;
    FirebaseAuth auth;
    String currentUserId;
    ArrayList<Group> list = new ArrayList<>();
    
    public GroupFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GroupView = inflater.inflate(R.layout.fragment_group, container, false);
        groupList = (RecyclerView) GroupView.findViewById(R.id.groupRecyclerView);
        groupList.setLayoutManager(new LinearLayoutManager(getContext()));
        //  rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        grpRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups").child(currentUserId);
        groupsMetsRef = FirebaseDatabase.getInstance().getReference().child("GroupsMeta");
        // Toast.makeText(getContext(), FirebaseInstanceId.getInstance().getToken(),Toast.LENGTH_LONG).show();
        updateToken(FirebaseInstanceId.getInstance().getToken());
      /*  String deviceToken = FirebaseInstanceId.getInstance().getToken();
        groupsMetsRef.child(currentUserId).setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });     */
        return GroupView;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())).setValue(token1);
        //reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Group>()
                .setQuery(grpRef, Group.class).build();

        FirebaseRecyclerAdapter<Group, GroupFragment.GroupViewHolder> adapter = new FirebaseRecyclerAdapter<Group, GroupFragment.GroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupFragment.GroupViewHolder holder, int position, @NonNull Group model) {
                String groupId = getRef(position).getKey();
                final Group[] group = {new Group()};
                groupsMetsRef.child(groupId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        group[0] = snapshot.getValue(Group.class);
                        /*String profilePic = snapshot.child("profilepic").getValue().toString();
                        String username = snapshot.child("userName").getValue().toString();
*/
                        holder.groupName.setText(group[0].getGroupName());
                        Picasso.get().load(group[0].getGroupIcon()).placeholder(R.drawable.ic_user).into(holder.image);
                        /*FirebaseDatabase.getInstance().getReference().child("chats").child(currentUserId+group[0].getUserId()).orderByChild("timestamp")
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
                        });*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), GroupChatActivity.class);
                        intent.putExtra("groupId", group[0].getGroupId());
                        intent.putExtra("groupIcon", group[0].getGroupIcon());
                        intent.putExtra("groupName", group[0].getGroupName());
                        intent.putExtra("groupDesc", group[0].getGroupDesc());
                        intent.putExtra("groupAdminId", group[0].getGroupAdminId());
                        //intent.putExtra("groupObject", (Serializable) group[0]);
                        getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public GroupFragment.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_show_user,parent,false);
                GroupFragment.GroupViewHolder viewHolder = new GroupFragment.GroupViewHolder(view);
                // updateToken(FirebaseInstanceId.getInstance().getToken());
                return viewHolder;
            }
        };
        groupList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView groupName, lastMessage;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            groupName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
