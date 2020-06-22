package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsapp.Model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    UserProfile receiveUser;
    FirebaseAuth mAuth;
    String current_user_id;
    DatabaseReference mDatabaseUser;
    ImageView userImage;
    TextView visit_user_name, visit_user_last_seen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        DataIntent();
        init();
        RetrieveUserInfo();


    }

    private void RetrieveUserInfo() {
        visit_user_name.setText(receiveUser.getName());
        visit_user_last_seen.setText(receiveUser.getStatus());
        if (receiveUser.getImage() == null) {
            userImage.setImageResource(R.drawable.default_user);
        } else {
            Picasso.get().load(receiveUser.getImage()).into(userImage);
        }
    }

    private void init() {

        toolbar = findViewById(R.id.chat_tool_bar);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);
        userImage = findViewById(R.id.custom_profile_image);
        visit_user_name = findViewById(R.id.custom_profile_name);
        visit_user_last_seen = findViewById(R.id.custom_user_last_seen);


    }

    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("userprofile")) {
                receiveUser = (UserProfile) intent.getSerializableExtra("userprofile");
            }

        }
    }
}