package com.example.whatsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapter.AdapterChatList;
import com.example.whatsapp.Adapter.AdapterFriend;
import com.example.whatsapp.Adapter.AdapterRequest;
import com.example.whatsapp.Model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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
public class RequestFragment extends Fragment {
    View view;
    List<String> userIDList;
    RecyclerView recyclerViewRequest;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private FirebaseUser current_user;
    private DatabaseReference mRequestDatabase, mUserDatabase;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userIDList = new ArrayList<>();
        view = inflater.inflate(R.layout.fragment_request, container, false);
        recyclerViewRequest = view.findViewById(R.id.recyclerRequest);
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser();


        if (current_user != null) {
            currentUserID = current_user.getUid();
            mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Chat Request").child(currentUserID);
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mRequestDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userIDList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        userIDList.add(dataSnapshot1.getKey().toString());
                    }
                    AdapterRequest adapterRequest = new AdapterRequest(getContext(), userIDList, currentUserID);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerViewRequest.setLayoutManager(layoutManager);
                    recyclerViewRequest.setAdapter(adapterRequest);
                    adapterRequest.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return view;

    }
}
