package com.example.chatapplication.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.ChatDetailActivity;
import com.example.chatapplication.DownloadImage;
import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static org.webrtc.ContextUtils.getApplicationContext;
/*
 class SaveImageHelper implements Target{

     private WeakReference<ContentResolver> contentResolverWeakReference;
     private String name;
     private String desc;

     public SaveImageHelper( ContentResolver contentResolverWeakReference,String name,String desc) {
         this.contentResolverWeakReference = new WeakReference<ContentResolver>(contentResolverWeakReference);
         this.name = name;
         this.desc = desc;
     }

     @Override
     public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
         ContentResolver r = contentResolverWeakReference.get();
         //if(r!=null)
         String bit = "nullBitmap";
         if(bitmap==null)
             System.out.println(bit);

         MediaStore.Images.Media.insertImage(r,bitmap,name,desc);

         //Open gallery after download
        /* Intent i = new Intent();
         i.setAction(Intent.ACTION_GET_CONTENT);
         i.setType("image/*");
         context.startActivities(Intent.createChooser(i,"select picture"));
         }*/

     /*
     @Override
     public void onBitmapFailed(Exception e, Drawable errorDrawable) {

     }

     @Override
     public void onPrepareLoad(Drawable placeHolderDrawable) {

     }
 }
 */

public class ChatAdapter extends  RecyclerView.Adapter {

    //private static final int PERMISSION_STORAGE_CODE = 1000;
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

       if (holder.getClass() == SenderViewHolder.class){
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
           //Retrieving Url
           String url = messagesModel.getMessage().toString();
          // downloadFile(url,context);
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
            else
                  return   SENDER_VIEW_TYPE;
        }
        else{

            if (messagesModels.get(position).getType().equals("image"))
                return RECEIVER_VIEW_TYPE_IMAGE;
            else
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


    /*
    public void downloadFile(String url,Context ctx){
           String filename = UUID.randomUUID().toString() + "jpg";
           Picasso.get()
                   .load(url)
                   .into(new SaveImageHelper(ctx.getApplicationContext().getContentResolver(),filename,"image downloaded"));

         }
         */

}
