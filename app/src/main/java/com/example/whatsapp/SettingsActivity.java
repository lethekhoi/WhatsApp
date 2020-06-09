package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    Button btnUpdateProfile;
    EditText edtUserName, edtUserStatus;
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        edtUserName = findViewById(R.id.edtUserName);
        edtUserStatus = findViewById(R.id.edtUserStatus);
        imgProfile = findViewById(R.id.imgProfile);
    }
}
