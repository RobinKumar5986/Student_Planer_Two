package com.example.studentplanertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.studentplanertwo.Models.Users;
import com.example.studentplanertwo.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        //-----Authentication and DataBases------//
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        //-------------------------------------//
        progressDialog=new ProgressDialog(SignUp.this);
        progressDialog.setTitle("Creating Account...");
        progressDialog.setMessage("Creating your account please be patient");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=binding.txtUser.getText().toString();
                String email=binding.txtEmail.getText().toString();
                String password=binding.txtPassword.getText().toString();
                
                if(!user.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Users users=new Users(email,password,user);
                                        String Id=task.getResult().getUser().getUid();
                                        //-------setting from the users constructor-----//
                                        database.getReference().child("Users").child(Id).setValue(users);
                                        Toast.makeText(SignUp.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(SignUp.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(SignUp.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(SignUp.this, "Please Enter Credential(s)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.txtHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}