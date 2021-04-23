package com.example.chatapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.webrtc.EglBase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.webrtc.ContextUtils.getApplicationContext;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private Context mContext;

    public  DownloadImage(Context context){
        mContext=context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String[] URL) {

        String imageURL = URL[0];

        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        if (result != null) {
            File dir = new File(mContext.getFilesDir(), "MyImages");
            if(!dir.exists()){
                dir.mkdir();
            }
            File destination = new File(dir, "image.jpg");

            try {
                destination.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(destination);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}