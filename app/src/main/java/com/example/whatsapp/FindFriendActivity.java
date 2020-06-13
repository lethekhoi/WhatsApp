package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.whatsapp.Adapter.AdapterFriend;
import com.example.whatsapp.Model.GroupDetail;
import com.example.whatsapp.Model.UserProfile;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFriendActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerViewFriend;
    private DatabaseReference mDatabase;
    List<UserProfile> userProfileList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        init();
        retrievingData();

    }

    private void retrievingData() {
        progressDialog.setTitle("Find Friend");
        progressDialog.setMessage("Please wait....!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        userProfileList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfileList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    UserProfile userProfile = dataSnapshot1.getValue(UserProfile.class);
                    userProfileList.add(userProfile);
                }
                AdapterFriend adapterFriend = new AdapterFriend(FindFriendActivity.this, userProfileList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FindFriendActivity.this);
                recyclerViewFriend.setLayoutManager(layoutManager);
                recyclerViewFriend.setAdapter(adapterFriend);
                progressDialog.dismiss();
                adapterFriend.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarFindFriend);
        recyclerViewFriend = findViewById(R.id.recyclerFriend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}