package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Model.GroupDetail;
import com.example.whatsapp.Model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private String sender_user_id, current_state, receiver_user_id;
    FirebaseAuth mAuth;
    UserProfile userProfile;
    ImageView imgProfile;
    TextView txtName, txtStatus;
    Button btnSendMessage, btnDeclineChatRequest;
    private DatabaseReference mChatDatabase, mContactDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        mContactDatabase = FirebaseDatabase.getInstance().getReference().child("Contacts");
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

        ManageChatRequest();
    }

    private void ManageChatRequest() {

        mChatDatabase.child(sender_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiver_user_id)) {
                    String request_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        current_state = "request_send";
                        btnSendMessage.setText("Cancel Chat Request");
                    } else if (request_type.equals("received")) {
                        current_state = "request_received";
                        btnSendMessage.setText("Accept Chat Request");
                        btnDeclineChatRequest.setVisibility(View.VISIBLE);
                        btnDeclineChatRequest.setEnabled(true);
                        btnDeclineChatRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelChatRequest();
                            }
                        });
                    }
                } else {
                    mContactDatabase.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(receiver_user_id)) {
                                current_state = "friend";
                                btnSendMessage.setText("Remove this contact");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!sender_user_id.equals(receiver_user_id)) {
            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnSendMessage.setEnabled(false);
                    if (current_state.equals("new")) {
                        SendChatRequest();
                    }
                    if (current_state.equals("request_send")) {
                        CancelChatRequest();
                    }
                    if (current_state.equals("request_received")) {
                        AcceptChatRequest();
                    }
                }
            });
        } else {
            btnSendMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void AcceptChatRequest() {
        mContactDatabase.child(sender_user_id).child(receiver_user_id)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mContactDatabase.child(receiver_user_id).child(sender_user_id)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mChatDatabase.child(sender_user_id).child(receiver_user_id)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mChatDatabase.child(receiver_user_id).child(sender_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    btnSendMessage.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    btnSendMessage.setText("Remove this contact");
                                                                                    btnDeclineChatRequest.setVisibility(View.INVISIBLE);
                                                                                    btnDeclineChatRequest.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest() {
        mChatDatabase.child(sender_user_id).child(receiver_user_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mChatDatabase.child(receiver_user_id).child(sender_user_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnSendMessage.setEnabled(true);
                                                current_state = "new";
                                                btnSendMessage.setText("Send Message");
                                                btnDeclineChatRequest.setVisibility(View.INVISIBLE);
                                                btnDeclineChatRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {
        mChatDatabase.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mChatDatabase.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnSendMessage.setEnabled(true);
                                                current_state = "request_send";
                                                btnSendMessage.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();
        imgProfile = findViewById(R.id.imgProfile);
        txtName = findViewById(R.id.user_profile_name);
        txtStatus = findViewById(R.id.user_profile_status);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        current_state = "new";
        receiver_user_id = userProfile.getUid();
        btnDeclineChatRequest = findViewById(R.id.btnDeclineMessage);
    }

    private void DataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("userprofile")) {
                userProfile = (UserProfile) intent.getSerializableExtra("userprofile");
            }

        }
    }
}