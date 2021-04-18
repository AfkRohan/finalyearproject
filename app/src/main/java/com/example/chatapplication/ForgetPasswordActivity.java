package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatapplication.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        binding.tvbacktosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        binding.btnResetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etforgetpasswordemail.getText().toString().trim();
                if(email.isEmpty()){
                    binding.etforgetpasswordemail.setError("Enter Your Email");
                }
                else{
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgetPasswordActivity.this, "Please Check Your Inbox", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(ForgetPasswordActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}