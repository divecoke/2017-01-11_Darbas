package com.example.ernestas.myapplication65651;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private Button bRegister;
    private EditText etEmail, etPassword;
    private TextView tvLoginHere;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        progressdialog = new ProgressDialog(this);

        if(firebaseAuth.getCurrentUser() != null) {
            //Profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        bRegister = (Button) findViewById(R.id.bLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvLoginHere = (TextView) findViewById(R.id.tvLoginHere);

        bRegister.setOnClickListener(this);
        tvLoginHere.setOnClickListener(this);
    }

    private void registerUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter the username", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressdialog.setMessage("Registering please wait..");
        progressdialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressdialog.dismiss();
                if(task.isSuccessful()) {
                    Toast.makeText(Register.this, "Register Successfull now you must fill all information", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent goInProfileSettings = new Intent(getApplicationContext(), ProfileSettings.class);
                    startActivity(goInProfileSettings);
                } else {
                    Toast.makeText(Register.this, "Sorry but this email already exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == bRegister) {
            registerUser();
        }
        if(v == tvLoginHere) {
            Intent goInLogin = new Intent(getApplicationContext(), Login.class);
            startActivity(goInLogin);
        }
    }
}
