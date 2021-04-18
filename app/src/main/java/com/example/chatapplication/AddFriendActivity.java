package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Adapters.AddFriendAdapter;
import com.example.chatapplication.Adapters.UsersAdapter;
import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.Models.Users;

import com.bumptech.glide.Glide;
import com.example.chatapplication.databinding.ActivityAddFriendBinding;
import com.example.chatapplication.databinding.ActivityChatDetailBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    RecyclerView mResultList;

    ActivityAddFriendBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFriendBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_add_friend);
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");


        final AddFriendAdapter adapter = new AddFriendAdapter(list,this);

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mResultList = (RecyclerView) findViewById(R.id.resultList);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        mResultList.setAdapter(adapter);

        /*binding.resultList.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.resultList.setLayoutManager(layoutManager);
        */

        //database.getReference().orderByChild("userName").startAt("Shah").endAt("Shah" + "\uf8ff")child("Users").
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(users);}
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                //firebaseUserSearch(searchText);

            }
        });

    }

   /* private void firebaseUserSearch(String searchText) {

        Query firebaseSearchQuery = mUserDatabase.orderByChild("userName").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Users> options=
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(firebaseSearchQuery, Users.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UsersViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false));

            }


            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder usersViewHolder, int position, @NonNull Users model) {

                usersViewHolder.setDetails(getApplicationContext(), model.getUserName(), model.getStatus(), model.getProfilepic());

                //usersViewHolder.setName(model.getUserName());
                //usersViewHolder.setImage(model.getProfilepic(),getApplicationContext());


            }
        };
        mResultList.setAdapter(firebaseRecyclerAdapter);

    }
    /*private void firebaseUserSearch(String searchText) {

        Toast.makeText(AddFriendActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("userName").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.list_layout,
                UsersViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {


                viewHolder.setDetails(getApplicationContext(), model.getUserName(), model.getStatus(), model.getProfilepic());

            }
        };
        mResultList.setAdapter(firebaseRecyclerAdapter);
    }*/
    // View Holder Class

   /* public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(Context ctx, String userName, String userStatus, String userImage){

            TextView user_name = (TextView) mView.findViewById(R.id.name_text);
            TextView user_status = (TextView) mView.findViewById(R.id.status_text);
            ImageView user_image = (ImageView) mView.findViewById(R.id.profile_image);


            user_name.setText(userName);
            user_status.setText(userStatus);

            Glide.with(ctx).load(userImage).into(user_image);
        }
    }*/


}