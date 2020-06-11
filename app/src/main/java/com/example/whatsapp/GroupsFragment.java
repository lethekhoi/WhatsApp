package com.example.whatsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapter.AdapterGroup;
import com.example.whatsapp.Model.GroupDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
    View view;
    DatabaseReference mDatabase;
    List<GroupDetail> groupDetailList;
    RecyclerView recyclerViewGroup;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups, container, false);
        groupDetailList = new ArrayList<>();

        recyclerViewGroup = view.findViewById(R.id.recyclerGroup);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Group");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupDetailList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    GroupDetail groupDetail = dataSnapshot1.getValue(GroupDetail.class);
                    groupDetailList.add(groupDetail);
                }

                AdapterGroup adapterGroup = new AdapterGroup(getContext(), groupDetailList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerViewGroup.setLayoutManager(layoutManager);
                recyclerViewGroup.setAdapter(adapterGroup);
                adapterGroup.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;


    }
}
