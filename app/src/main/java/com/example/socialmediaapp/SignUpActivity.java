package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.socialmediaapp.Model.UserModel;
import com.example.socialmediaapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.createUserWithEmailAndPassword(binding.email.getEditText().getText().toString(), binding.password.getEditText().getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    UserModel user = new UserModel(binding.fullname.getEditText().getText().toString(), binding.profession.getEditText().getText().toString(), binding.email.getEditText().getText().toString(), binding.password.getEditText().getText().toString());

                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    Toast.makeText(SignUpActivity.this, "user data saved", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this,HomeActivity.class);
                                    startActivity(intent);

                                }
                                else{
                                    Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
        binding.AlreadyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, Login.class);
                startActivity(intent);

            }
        });
    }
}