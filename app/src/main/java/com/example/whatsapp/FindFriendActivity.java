package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class FindFriendActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerViewFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarFindFriend);
        recyclerViewFriend = findViewById(R.id.recyclerFriend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friend");
    }
}