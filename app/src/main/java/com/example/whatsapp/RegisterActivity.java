package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    Button btnRegister;
    Pattern pattern;
    Matcher matcher;
    EditText edtEmailRegister, edtPasswordRegister;
    TextView txthaveanaccount;
    TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    Boolean kiemtrathongtin = false;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        txthaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(iLogin);

            }
        });


        edtEmailRegister.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b) {

                    String chuoi = edtEmailRegister.getText().toString();

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
        edtPasswordRegister.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String chuoi = edtPasswordRegister.getText().toString();
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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtEmailRegister.getText().toString();
                String password = edtPasswordRegister.getText().toString();

                if (kiemtrathongtin) {
                    progressDialog.setTitle("Đang tạo tài khoản");
                    progressDialog.setMessage("Vui lòng chờ trong giây lát");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        String currentUserID = mAuth.getCurrentUser().getUid();
                                        mDatabase.child("users").child(currentUserID).setValue("");
                                        SendUsertoMainActivity();
                                        Toast.makeText(RegisterActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });


                }
            }
        });
    }


    private void init() {
        btnRegister = findViewById(R.id.btnRegister);
        edtEmailRegister = findViewById(R.id.edtEmailRegister);
        edtPasswordRegister = findViewById(R.id.edtPassRegister);
        txthaveanaccount = findViewById(R.id.txtHaveacount);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        progressDialog = new ProgressDialog(this);
    }

    private void SendUsertoMainActivity() {
        Intent iMain = new Intent(RegisterActivity.this, MainActivity.class);
        iMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iMain);
        finish();
    }
}
