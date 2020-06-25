package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Adapter.AdapterMessage;
import com.example.whatsapp.Model.Message;
import com.example.whatsapp.Model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    UserProfile receiveUser;
    FirebaseAuth mAuth;
    String messageSenderID, messageReceiverID;
    DatabaseReference mDatabase;
    ImageView userImage;

    TextView visit_user_name, visit_user_last_seen;
    EditText messageTextInput;
    ImageView btnSendMessage;
    RecyclerView recyclerViewChatList;
    private final List<Message> messageList = new ArrayList<>();
    private AdapterMessage adapterMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        DataIntent();
        init();
        RetrieveUserInfo();
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        messageList.clear();
        mDatabase.child("Message").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        messageList.add(message);
                        adapterMessage.notifyDataSetChanged();
                        recyclerViewChatList.smoothScrollToPosition(recyclerViewChatList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void SendMessage() {
        final String messageText = messageTextInput.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "Message/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Message/" + messageReceiverID + "/" + messageSenderID;
            DatabaseReference userMessageKeyRef = mDatabase.child("Message")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("messageID", messagePushID);

            Map messageBodyDetail = new HashMap();
            messageBodyDetail.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetail.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            mDatabase.updateChildren(messageBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {


                    } else {
                        Toast.makeText(ChatActivity.this, "Gửi thất bại", Toast.LENGTH_SHORT).show();
                    }
                    messageTextInput.setText("");
                }
            });
        }
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        messageTextInput = findViewById(R.id.edtmessage);
        btnSendMessage = findViewById(R.id.imageButtonSend);
        toolbar = findViewById(R.id.chat_tool_bar);
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        messageReceiverID = receiveUser.getUid();
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

        adapterMessage = new AdapterMessage(messageList, ChatActivity.this);
        recyclerViewChatList = findViewById(R.id.recyclerMessageList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerViewChatList.setLayoutManager(layoutManager);
        recyclerViewChatList.setAdapter(adapterMessage);

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