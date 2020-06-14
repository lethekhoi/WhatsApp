package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Model.GroupDetail;
import com.example.whatsapp.Model.UserProfile;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    UserProfile userProfile;
    ImageView imgProfile;
    TextView txtName, txtStatus;
    Button btnSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        DataIntent();
        init();
        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        txtName.setText(userProfile.getName());
        txtStatus.setText(userProfile.getStatus());
        if (userProfile.getImage() == null) {
            imgProfile.setImageResource(R.drawable.default_user);
        } else {
            Picasso.get().load(userProfile.getImage()).into(imgProfile);
        }
    }

    private void init() {
        imgProfile = findViewById(R.id.imgProfile);
        txtName = findViewById(R.id.user_profile_name);
        txtStatus = findViewById(R.id.user_profile_status);
        btnSendMessage = findViewById(R.id.btnSendMessage);
    }

    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("userprofile")) {
                userProfile = (UserProfile) intent.getSerializableExtra("userprofile");
                Toast.makeText(this, userProfile.getName(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}