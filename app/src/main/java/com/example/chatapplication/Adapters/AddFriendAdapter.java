package com.example.chatapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.ChatDetailActivity;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {
    ArrayList<Users> list;
    Context context;


    public AddFriendAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.ic_user).into(holder.image);
        holder.userName.setText(users.getUserName());
        holder.status.setText(users.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("UsersFriend")
                        .child(FirebaseAuth.getInstance().getUid()).child(users.getUserId())
                        .child("isFriend").setValue("Yes")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference().child("UsersFriend")
                                            .child(users.getUserId()).child(FirebaseAuth.getInstance().getUid())
                                            .child("isFriend").setValue("Yes")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        Intent intent = new Intent(context, ChatDetailActivity.class);
                                                        intent.putExtra("userId",users.getUserId());
                                                        intent.putExtra("profilePic",users.getProfilepic());
                                                        intent.putExtra("userName",users.getUserName());
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });


            }
        });

    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView userName, status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.name_text);
            status = itemView.findViewById(R.id.status_text);

        }
    }
}
