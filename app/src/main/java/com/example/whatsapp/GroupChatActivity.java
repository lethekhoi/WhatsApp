package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Model.GroupDetail;

public class GroupChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton imageButtonSend;
    EditText edtmessage;
    ScrollView scrollView;
    TextView displayTextMessage;
    GroupDetail groupDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        DataIntent();
        init();


    }


    private void init() {
        toolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(groupDetail.getName());

        imageButtonSend = findViewById(R.id.imageButtonSend);
        edtmessage = findViewById(R.id.edtmessage);
        scrollView = findViewById(R.id.my_scroll_view);
        displayTextMessage = findViewById(R.id.group_chat_text_display);
    }

    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("groupdetail")) {
                groupDetail = (GroupDetail) intent.getSerializableExtra("groupdetail");
            }

        }
    }
}