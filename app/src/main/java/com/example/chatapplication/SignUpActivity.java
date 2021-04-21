package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.chatapplication.Models.Users;
import com.example.chatapplication.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.etuserName.getText().toString();
                String email = binding.etemail.getText().toString();
                String password = binding.etPassword.getText().toString();
                if( userName.length() == 0 ){
                    binding.etuserName.requestFocus();
                    binding.etuserName.setError("Username can not be empty");
                }else if(!userName.matches("[a-zA-Z]+")){
                    binding.etuserName.requestFocus();
                    binding.etuserName.setError("Enter only alphabetical character.");
                }else if(email.length() == 0){
                    binding.etemail.requestFocus();
                    binding.etemail.setError("Email Address can not be empty");
                }else if(!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")){
                    binding.etemail.requestFocus();
                    binding.etemail.setError("Enter valid email");
                } else if(password.length() < 8){
                    binding.etPassword.requestFocus();
                    binding.etPassword.setError("Password can not be less than 8");
                }else {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword
                            (binding.etemail.getText().toString(), binding.etPassword.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Users user = new Users(binding.etuserName.getText().toString(), binding.etemail.getText().toString(),
                                                binding.etPassword.getText().toString());
                                        String id = task.getResult().getUser().getUid();
                                        user.setUserId(id);
                                        database.getReference().child("Users").child(id).setValue(user);

                                       Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                 /*   Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent); */
                }
            }
        });
        binding.tvalreadyhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }

}