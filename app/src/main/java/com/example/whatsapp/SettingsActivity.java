package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class SettingsActivity extends AppCompatActivity {
    Button btnUpdateProfile;
    EditText edtUserName, edtUserStatus;
    ImageView imgProfile;
    TextInputLayout textInputLayoutUserName, textInputLayoutStatus;
    boolean kiemtrathongtin = false;
    String currentUserID;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        RetrieveUserInfo();


        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String chuoi = edtUserName.getText().toString();
                    if (chuoi.trim().equals("") || chuoi.equals(null)) {
                        textInputLayoutUserName.setError("Bạn chưa nhập tên người dùng");
                        textInputLayoutUserName.setErrorEnabled(true);
                        kiemtrathongtin = false;
                    } else {
                        textInputLayoutUserName.setError("");
                        textInputLayoutUserName.setErrorEnabled(false);
                        kiemtrathongtin = true;
                    }
                }
            }
        });
        edtUserStatus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String chuoi = edtUserStatus.getText().toString();
                    if (chuoi.trim().equals("") || chuoi.equals(null)) {
                        textInputLayoutStatus.setError("Bạn chưa nhập giới thiệu");
                        textInputLayoutStatus.setErrorEnabled(true);
                        kiemtrathongtin = false;
                    } else {
                        textInputLayoutStatus.setError("");
                        textInputLayoutStatus.setErrorEnabled(false);
                        kiemtrathongtin = true;
                    }
                }
            }
        });


        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUserName.getText().toString().trim();
                String status = edtUserStatus.getText().toString().trim();

                if (kiemtrathongtin) {
                    HashMap<String, String> profileMap = new HashMap<>();
                    profileMap.put("uid", currentUserID);
                    profileMap.put("name", username);
                    profileMap.put("status", status);

                    mDatabase.child("users").child(currentUserID).setValue(profileMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "Error. Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }


    private void RetrieveUserInfo() {
        mDatabase.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status") && dataSnapshot.hasChild("image")) {
                    String image = dataSnapshot.child("image").getValue().toString();
                    edtUserName.setText(dataSnapshot.child("name").getValue().toString());
                    edtUserStatus.setText(dataSnapshot.child("status").getValue().toString());
                } else if (dataSnapshot.child("name").exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")) {
                    edtUserName.setText(dataSnapshot.child("name").getValue().toString());
                    edtUserStatus.setText(dataSnapshot.child("status").getValue().toString());
                } else {
                    edtUserName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please Update your profile name  ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        edtUserName = findViewById(R.id.edtUserName);
        edtUserStatus = findViewById(R.id.edtUserStatus);
        imgProfile = findViewById(R.id.imgProfile);
        textInputLayoutUserName = findViewById(R.id.textInputLayoutUserName);
        textInputLayoutStatus = findViewById(R.id.textInputLayoutStatus);
    }

}
