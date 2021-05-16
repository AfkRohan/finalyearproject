 package com.example.chatapplication.Adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Models.MessagesModel;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends  RecyclerView.Adapter {

    //private static final int PERMISSION_STORAGE_CODE = 1000;
    ArrayList <MessagesModel> messagesModels;
    Context context;
    String recId,senId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    int SENDER_VIEW_TYPE_IMAGE=3;
    int RECEIVER_VIEW_TYPE_IMAGE=4;
    private static final int CREATE_FILE = 5;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context, String recId,String senId) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recId = recId;
        this.senId = senId;
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
                     dialog.dismiss();
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
           String url = messagesModel.getMessage();
           Picasso.get().load(url).centerCrop().resize(250,250).into(((SenderViewHolderImage)holder).senderImg);
           SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
           String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
           ((SenderViewHolderImage) holder).senderTime.setText(timeString);
       }

       else if(holder.getClass()==ReceiverViewHolderImage.class) {
           //Retrieving Url
           String chatroom=senId+recId;
           String url = messagesModel.getMessage();

                if(("true").equals(messagesModel.isFirst())){
                    ((ReceiverViewHolderImage)holder).receiverImg.setImageResource(R.drawable.ic_baseline_image_24);
                    ((ReceiverViewHolderImage)holder).receiverBtn.setVisibility(View.VISIBLE);
               ((ReceiverViewHolderImage) holder).receiverBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       new SaveImageToPhone(context, ((ReceiverViewHolderImage) holder).progressBar, messagesModel,chatroom).execute(url);
                       ((ReceiverViewHolderImage)holder).receiverBtn.setVisibility(View.INVISIBLE);
                       Picasso.get().load(url).resize(250,250).centerCrop().into(((ReceiverViewHolderImage) holder).receiverImg);
                   }
               });
              }
                else {
                    Picasso.get().load(url).resize(250,250).centerCrop().into(((ReceiverViewHolderImage) holder).receiverImg);
                }

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
    /*
    private void saveToGallery(Bitmap myImage,Context context) {
        ContentResolver cr = context.getContentResolver();
        String title = "myBitmap";
        String description = "My bitmap created by Android-er";
        String savedURL = MediaStore.Images.Media
                .insertImage(cr, myImage, title, description);
        //Toast.makeText(context.this,savedURL,Toast.LENGTH_LONG).show();
    }*/

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
        ProgressBar progressBar;
        ImageView receiverImg;
        TextView receiverTime;
        ImageView receiverBtn;
        public ReceiverViewHolderImage(@NonNull View itemView) {
            super(itemView);
            receiverImg = itemView.findViewById(R.id.receiverImageView);
            receiverTime = itemView.findViewById(R.id.receiverImageTime);
            receiverBtn = itemView.findViewById(R.id.downloadImage);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public  class  SenderViewHolder extends  RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
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

class SaveImageToPhone extends AsyncTask<String,Void,Bitmap> {
    private final MessagesModel messagesModel;
    private String chatroom;
    private Context context;
    private ProgressBar progressBar;
    public SaveImageToPhone(Context context, ProgressBar progressBar, MessagesModel messagesModel, String chatroom){
        this.context=context;
        this.progressBar = progressBar;
        this.messagesModel=messagesModel;
        this.chatroom=chatroom;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String []strings) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            connection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(Bitmap myImage) {
        super.onPostExecute(myImage);
        ContentResolver cr = context.getContentResolver();
        Timestamp temp = new Timestamp(new Date().getTime());
        String imageName = temp.toString();
        String title = "IMG-"+ imageName;
        String description = "Image received";
        String savedURL = MediaStore.Images.Media
                .insertImage(cr, myImage, title, description);
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatroom).child(messagesModel.getMessageId()).child("message").setValue(savedURL);
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatroom).child(messagesModel.getMessageId()).child("first").setValue("false");
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(context,"Image saved in gallery",Toast.LENGTH_SHORT).show();
    }
}