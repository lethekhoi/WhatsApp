package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

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
        mAuth = FirebaseAuth.getInstance();


        btnPhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iPhoneLogin = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(iPhoneLogin);
            }
        });


        txtNeesNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(iRegister);
            }
        });
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b) {

                    String chuoi = userEmail.getText().toString();

                    if (chuoi.trim().equals("") || chuoi.equals(null)) {
                        textInputLayoutEmail.setError("Bạn chưa nhập địa chỉ Email");
                        textInputLayoutEmail.setEnabled(true);
                        kiemtrathongtin = false;

                    } else {

                        Boolean kiemtraemail = Patterns.EMAIL_ADDRESS.matcher(chuoi).matches();
                        if (!kiemtraemail) {
                            textInputLayoutEmail.setError("Đây không phải địa chỉ Email");
                            textInputLayoutEmail.setErrorEnabled(true);
                            kiemtrathongtin = false;


                        } else {
                            textInputLayoutEmail.setError("");
                            textInputLayoutEmail.setErrorEnabled(false);
                            kiemtrathongtin = true;

                        }
                    }
                }
            }
        });
        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String chuoi = userPassword.getText().toString();
                    if (chuoi.trim().equals("") || chuoi.equals(null)) {
                        textInputLayoutPassword.setError("Bạn chưa nhập mật khẩu");
                        textInputLayoutPassword.setErrorEnabled(true);
                        kiemtrathongtin = false;
                    } else {
                        textInputLayoutPassword.setError("");
                        textInputLayoutPassword.setErrorEnabled(false);
                        kiemtrathongtin = true;
                    }
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();
                if (kiemtrathongtin) {
                    progressDialog.setTitle("Đang đăng nhập");
                    progressDialog.setMessage("Vui lòng chờ trong giây lát");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        SendUsertoMainActivity();
                                        progressDialog.dismiss();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });

    }


    private void SendUsertoMainActivity() {
        Intent iMain = new Intent(LoginActivity.this, MainActivity.class);
        iMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iMain);
        finish();
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
