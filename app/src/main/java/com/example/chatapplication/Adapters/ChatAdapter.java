package com.example.chatapplication.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends  RecyclerView.Adapter {

    ArrayList <MessagesModel> messagesModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    int SENDER_VIEW_TYPE_IMAGE=3;
    int RECEIVER_VIEW_TYPE_IMAGE=4;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context, String recId) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==SENDER_VIEW_TYPE) {
           View  view = LayoutInflater.from( context ).inflate(R.layout.sample_sender ,parent ,false);
           return new SenderViewHolder(view);
        }
        else if(viewType==SENDER_VIEW_TYPE_IMAGE)
        {
            View  view = LayoutInflater.from( context ).inflate(R.layout.sample_sender_image ,parent ,false);
            return new SenderViewHolderImage(view);
        }
        else if(viewType==RECEIVER_VIEW_TYPE_IMAGE){
            View  view = LayoutInflater.from( context ).inflate(R.layout.sample_receiver_image ,parent ,false);
            return new ReceiverViewHolderImage(view);
        }
        else {
            View  view;
            view = LayoutInflater.from( context ).inflate(R.layout.sample_receiver ,parent ,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
     MessagesModel messagesModel = messagesModels.get(position);

     holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View v) {
             new AlertDialog.Builder(context).setTitle("Delete").setMessage("Are you sure you want to delete this message")
                     .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             FirebaseDatabase database = FirebaseDatabase.getInstance();
                             String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                             database.getReference().child("chats").child(senderRoom)
                                     .child(messagesModel.getMessageId()).setValue(null);
                         }
                     }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();;
                 }
             }).show();

             return false;
         }
     });

       if(holder.getClass() == SenderViewHolder.class){
           ((SenderViewHolder) holder).senderMsg.setText(messagesModel.getMessage());
           SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
           String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
           ((SenderViewHolder) holder).senderTime.setText(timeString);
       }

       else if(holder.getClass()==SenderViewHolderImage.class){
           String url = messagesModel.getMessage().toString();
           Picasso.get().load(url).into(((SenderViewHolderImage)holder).senderImg);
           SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
           String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
           ((SenderViewHolderImage) holder).senderTime.setText(timeString);
       }
       else if(holder.getClass()==ReceiverViewHolderImage.class){
           String url = messagesModel.getMessage().toString();
           Picasso.get().load(url).into(((ReceiverViewHolderImage)holder).receiverImg);
           SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
           String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
           ((ReceiverViewHolderImage) holder).receiverTime.setText(timeString);
       }
       else {
           ((ReceiverViewHolder)holder).receiverMsg.setText(messagesModel.getMessage());
           SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
           String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
           ((ReceiverViewHolder) holder).receiverTime.setText(timeString);
       }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getId().equals(FirebaseAuth.getInstance().getUid())){

            if(messagesModels.get(position).getType().equals("image"))
                  return SENDER_VIEW_TYPE_IMAGE;
            return   SENDER_VIEW_TYPE;
        }
        else{

            if (messagesModels.get(position).getType().equals("image"))
                return RECEIVER_VIEW_TYPE_IMAGE;
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMsg,receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }


    public class ReceiverViewHolderImage extends RecyclerView.ViewHolder {

        ImageView receiverImg;
        TextView receiverTime;

        public ReceiverViewHolderImage(@NonNull View itemView) {
            super(itemView);
            receiverImg = itemView.findViewById(R.id.receiverImageView);
            receiverTime = itemView.findViewById(R.id.receiverImageTime);
        }
    }

    public  class  SenderViewHolder extends  RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.sendetTime);
        }
    }


    public class SenderViewHolderImage extends RecyclerView.ViewHolder {

        ImageView senderImg;
        TextView senderTime;

        public SenderViewHolderImage(@NonNull View itemView) {
            super(itemView);
            senderImg = itemView.findViewById(R.id.senderImageView);
            senderTime = itemView.findViewById(R.id.senderImageTime);
        }
    }


}
