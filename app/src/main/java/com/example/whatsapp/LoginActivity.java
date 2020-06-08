package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    FirebaseUser currentUser;
    Button btnLogin, btnPhoneLogin;
    EditText userEmail, userPassword;
    TextView txtNeesNewAccount, txtForgetPassword;
    TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    boolean kiemtrathongtin = false;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        txtNeesNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(iRegister);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser!=null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent iMain= new Intent(LoginActivity.this, MainActivity.class);
        startActivity(iMain);
    }
    private void init() {
        btnLogin = findViewById(R.id.cirLoginButton);
        btnPhoneLogin = findViewById(R.id.LoginPhone);
        userEmail = findViewById(R.id.editTextEmail);
        userPassword = findViewById(R.id.editTextPassword);
        txtNeesNewAccount = findViewById(R.id.newaccount);
        txtForgetPassword = findViewById(R.id.forgetpassword);
        textInputLayoutEmail = findViewById(R.id.textInputEmail);
        textInputLayoutPassword = findViewById(R.id.textInputPassword);
        progressDialog = new ProgressDialog(this);
    }
}
