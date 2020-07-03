package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.usage.StorageStats;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Adapter.AdapterMessage;
import com.example.whatsapp.Model.Message;
import com.example.whatsapp.Model.UserProfile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    ImageButton btnSendFile;
    TextView visit_user_name, visit_user_last_seen;
    EditText messageTextInput;
    ImageView btnSendMessage;
    RecyclerView recyclerViewChatList;
    private final List<Message> messageList = new ArrayList<>();
    private AdapterMessage adapterMessage;
    String saveCurrentTime, saveCurrentDay;
    private ProgressDialog loadingBar;
    String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;

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
        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "Ms Word Files"

                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                        } else if (i == 1) {
                            checker = "pdf";
                        } else if (i == 2) {
                            checker = "docx";
                        }
                    }
                });
                builder.show();
            }
        });

        mDatabase.child("Message").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        messageList.add(message);
                        adapterMessage.notifyDataSetChanged();
                        recyclerViewChatList.getLayoutManager().setAutoMeasureEnabled(false);
                        recyclerViewChatList.getLayoutManager().setMeasurementCacheEnabled(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 438 && resultCode == RESULT_OK && data != null & data.getData() != null) {
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait,...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fileUri = data.getData();
            if (!checker.equals("image")) {

            } else if (checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                final String messageSenderRef = "Message/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Message/" + messageReceiverID + "/" + messageSenderID;
                DatabaseReference userMessageKeyRef = mDatabase.child("Message")
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", messageSenderID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDay);


                            Map messageBodyDetail = new HashMap();
                            messageBodyDetail.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetail.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            mDatabase.updateChildren(messageBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Gửi thành công hình ảnh", Toast.LENGTH_SHORT).show();

                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Gửi thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                    messageTextInput.setText("");
                                }
                            });
                        }
                    }
                });

            } else {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing selected, Error", Toast.LENGTH_SHORT).show();
            }
        }
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
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDay);


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
        loadingBar = new ProgressDialog(this);
        btnSendFile = findViewById(R.id.send_files_btn);
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
        DisplayLastSeen();
        adapterMessage = new AdapterMessage(messageList, ChatActivity.this);
        recyclerViewChatList = findViewById(R.id.recyclerMessageList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerViewChatList.setLayoutManager(layoutManager);
        recyclerViewChatList.setAdapter(adapterMessage);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDay = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDay = currentDay.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

    }

    private void DisplayLastSeen() {
        mDatabase.child("users").child(receiveUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("userState").hasChild("state")) {
                    String date = dataSnapshot.child("userState").child("day").getValue().toString();
                    String time = dataSnapshot.child("userState").child("time").getValue().toString();
                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
                    if (state.equals("online")) {
                        visit_user_last_seen.setText("online");
                    } else if (state.equals("offline")) {
                        visit_user_last_seen.setText("Last seen :" + date + " " + time);
                    }
                } else {
                    visit_user_last_seen.setText("offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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