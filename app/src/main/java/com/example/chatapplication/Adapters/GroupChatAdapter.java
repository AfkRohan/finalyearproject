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

public class GroupChatAdapter extends  RecyclerView.Adapter {

    ArrayList <MessagesModel> messagesModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    int SENDER_VIEW_TYPE_IMAGE = 3;
    int RECEIVER_VIEW_TYPE_IMAGE = 4;

    public GroupChatAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    public GroupChatAdapter(ArrayList<MessagesModel> messagesModels, Context context, String recId) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==SENDER_VIEW_TYPE ){
            View  view = LayoutInflater.from( context ).inflate(R.layout.sample_group_sender ,parent ,false);
            return new SenderViewHolder(view);
        }
        else if (viewType==SENDER_VIEW_TYPE_IMAGE ){
            View  view = LayoutInflater.from( context ).inflate(R.layout.sample_group_sender_image ,parent ,false);
            return new SenderViewHolderImageG(view);
        }
        else if (viewType==RECEIVER_VIEW_TYPE_IMAGE ){
            View  view = LayoutInflater.from( context ).inflate(R.layout.sample_group_receiver_image ,parent ,false);
            return new ReceiverViewHolderImageG(view);
        }
        else {
            View  view;
            view = LayoutInflater.from( context ).inflate(R.layout.sample_group_receiver ,parent ,false);
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
        else if(holder.getClass() == SenderViewHolderImageG.class){
            String url = messagesModel.getMessage();
            Picasso.get().load(url).centerCrop().resize(300,400).into(((SenderViewHolderImageG)holder).senderImg);
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
            ((SenderViewHolderImageG) holder).senderTime.setText(timeString);
        }
        else if(holder.getClass() == ReceiverViewHolderImageG.class){
            String url = messagesModel.getMessage();
            ((ReceiverViewHolderImageG)holder).receiverName.setText(messagesModel.getUserName());
            Picasso.get().load(url).centerCrop().resize(300,400).into(((ReceiverViewHolderImageG) holder).receiverImg);
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
            ((ReceiverViewHolderImageG) holder).receiverTime.setText(timeString);
            ((ReceiverViewHolderImageG)holder).receiverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SaveImage(context,((ReceiverViewHolderImageG)holder).progressBar).execute(url);
                }
            });
        }
        else {
            ((ReceiverViewHolder)holder).receiverName.setText(messagesModel.getUserName());
            ((ReceiverViewHolder)holder).receiverMsg.setText(messagesModel.getMessage());
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String timeString = formatter.format(new Date(messagesModel.getTimestamp()));
            ((ReceiverViewHolder) holder).receiverTime.setText(timeString);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getId().equals(FirebaseAuth.getInstance().getUid())) {
            if (messagesModels.get(position).getType().equals("image"))
                return SENDER_VIEW_TYPE_IMAGE;
            else
                return SENDER_VIEW_TYPE;
        }
        else {
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

        TextView receiverName,receiverMsg,receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverName = itemView.findViewById(R.id.receiverName);
            receiverMsg = itemView.findViewById(R.id.receiverTextG);
            receiverTime = itemView.findViewById(R.id.receiverTimeG);

        }
    }

    public  class  ReceiverViewHolderImageG extends  RecyclerView.ViewHolder{
        TextView receiverTime,receiverName;
        ImageView receiverImg,receiverBtn;
        ProgressBar progressBar;
        public ReceiverViewHolderImageG(@NonNull View itemView) {
            super(itemView);
            //senderName = itemView.findViewById(R.id.senderName)
            progressBar = itemView.findViewById(R.id.progressBar3);
            receiverBtn = itemView.findViewById(R.id.saveImage);
            receiverImg =  itemView.findViewById(R.id.Groupimagereceived);
            receiverName  = itemView.findViewById(R.id.receiverNameG);
            receiverTime = itemView.findViewById(R.id.receiverTimeG);
        }
    }

    public  class  SenderViewHolderImageG extends  RecyclerView.ViewHolder{
        TextView senderTime;
        ImageView senderImg;
        public SenderViewHolderImageG(@NonNull View itemView) {
            super(itemView);
            //senderName = itemView.findViewById(R.id.senderName);
            senderImg  = itemView.findViewById(R.id.senderImageG);
            senderTime = itemView.findViewById(R.id.senderTimeG);
        }
    }

    public  class  SenderViewHolder extends  RecyclerView.ViewHolder{
        TextView senderName,senderMsg,senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            //senderName = itemView.findViewById(R.id.senderName);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }

    class SaveImage extends AsyncTask<String,Void, Bitmap> {
        private Context context;
        private ProgressBar progressBar;
        public SaveImage(Context context, ProgressBar progressBar){
            this.context=context;
            this.progressBar = progressBar;
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
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(context,"Image saved in gallery",Toast.LENGTH_SHORT).show();
        }
    }
}
