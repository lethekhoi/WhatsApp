package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    Button btnSendVerifyCode, btnVerify;
    EditText edtPhoneNumber, edtVerifyCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        init();
        mAuth = FirebaseAuth.getInstance();
        btnSendVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnVerify.setVisibility(View.VISIBLE);
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneLoginActivity.this, "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Phone Verification");
                    progressDialog.setMessage("Please wait, while we are authenticating your phone");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = edtVerifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(PhoneLoginActivity.this, "Bạn chưa nhập mã xác nhận", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Verification Code");
                    progressDialog.setMessage("Please wait, while we are verification code");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
              //  signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                String mess = e.getMessage().toString();
                Toast.makeText(PhoneLoginActivity.this, mess, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                mVerificationId = verificationId;
                mResendToken = token;
                progressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code đã được gửi. Vùi lòng chờ", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        } else {
                            String mess = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, mess, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent iMain = new Intent(PhoneLoginActivity.this, MainActivity.class);
        iMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iMain);
        finish();
    }


    private void init() {
        btnSendVerifyCode = findViewById(R.id.btnSendCode);
        btnVerify = findViewById(R.id.btnVerify);
        edtPhoneNumber = findViewById(R.id.edtPhone);
        edtVerifyCode = findViewById(R.id.editVerifyCode);
        progressDialog = new ProgressDialog(this);
    }
}