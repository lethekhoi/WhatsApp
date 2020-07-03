package com.example.whatsapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapter.AdapterChatList;
import com.example.whatsapp.Model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */


public class ChatFragment extends Fragment {
    View view;
    RecyclerView recyclerViewChatList;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private FirebaseUser current_user;
    private DatabaseReference mDatabaseContract, mDatabaseUser;
    List<UserProfile> chatList;
    List<String> userIDList;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerViewChatList = view.findViewById(R.id.recyclerChatList);
        chatList = new ArrayList<>();
        userIDList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser();
        currentUserID = current_user.getUid();
        currentUserID = current_user.getUid();
        Log.d("ooo", "create");
        if (current_user != null) {
            mDatabaseContract = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");


            mDatabaseContract.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        userIDList.add(dataSnapshot1.getKey());

                    }
                    AdapterChatList adapterChatList = new AdapterChatList(getContext(), userIDList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerViewChatList.setLayoutManager(layoutManager);
                    recyclerViewChatList.setAdapter(adapterChatList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        return view;
    }


}
