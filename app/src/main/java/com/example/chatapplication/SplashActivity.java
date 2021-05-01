package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashActivity.this,SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };thread.start();
    }
}