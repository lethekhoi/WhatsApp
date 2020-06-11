package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Model.GroupDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton imageButtonSend;
    EditText edtmessage;
    ScrollView scrollView;
    TextView displayTextMessage;
    GroupDetail groupDetail;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase, groupDatabase, groupMessageKeyDatabase;
    String currentUserID, currentUserName, currentDate, currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        DataIntent();
        GetCurrentUserInfo();
        init();
        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessageInfotoDatabase();
                edtmessage.setText("");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        groupDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    DisplayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    DisplayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();
            displayTextMessage.append(chatName + ":\n" + chatMessage + ":\n" + chatDate + ":\n" + chatTime + "\n\n");

        }
    }

    private void SendMessageInfotoDatabase() {
        String message = edtmessage.getText().toString().trim();
        String messageKEY = groupDatabase.push().getKey();
        if (TextUtils.isEmpty(message)) {

        } else {
            Calendar ccalForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MM dd, yyyy");
            currentDate = currentDateFormat.format(ccalForDate.getTime());

            Calendar ccalForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(ccalForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupDatabase.updateChildren(groupMessageKey);

            groupMessageKeyDatabase = groupDatabase.child(messageKEY);
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            groupMessageKeyDatabase.updateChildren(messageInfoMap);
        }
    }

    private void GetCurrentUserInfo() {
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        groupDatabase = FirebaseDatabase.getInstance().getReference().child("Group").child(groupDetail.getName()).child("Conversation");

        mDatabase.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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