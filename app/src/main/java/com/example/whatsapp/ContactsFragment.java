package com.example.whatsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapter.AdapterFriend;
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
public class ContactsFragment extends Fragment {
    private View view;
    private RecyclerView myContactsList;
    List<UserProfile> userProfileList;
    private DatabaseReference ContacsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private FirebaseUser current_user;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        userProfileList = new ArrayList<>();

        myContactsList = (RecyclerView) view.findViewById(R.id.recyclerContact);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));


        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser();

        currentUserID = current_user.getUid();
        if (current_user != null) {
            ContacsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
            UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
            userProfileList.clear();

            ContacsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        UsersRef.child(dataSnapshot1.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                                userProfileList.add(userProfile);
                                AdapterFriend adapterFriend = new AdapterFriend(getContext(), userProfileList);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                myContactsList.setLayoutManager(layoutManager);
                                myContactsList.setAdapter(adapterFriend);
                                adapterFriend.notifyDataSetChanged();
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


        }


        return view;
    }

//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (current_user != null) {
//            FirebaseRecyclerOptions options =
//                    new FirebaseRecyclerOptions.Builder<UserProfile>()
//                            .setQuery(ContacsRef, UserProfile.class)
//                            .build();
//            final FirebaseRecyclerAdapter<UserProfile, ContactsViewHolder> adapter
//                    = new FirebaseRecyclerAdapter<UserProfile, ContactsViewHolder>(options) {
//                @Override
//                protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull UserProfile model) {
//                    final String userIDs = getRef(position).getKey();
//
//                    UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//
//
//                                String profileName = dataSnapshot.child("name").getValue().toString();
//                                String profileStatus = dataSnapshot.child("status").getValue().toString();
//
//                                holder.userName.setText(profileName);
//                                holder.userStatus.setText(profileStatus);
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//
//                @NonNull
//                @Override
//                public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_layout_users, viewGroup, false);
//                    ContactsViewHolder viewHolder = new ContactsViewHolder(view);
//                    return viewHolder;
//                }
//            };
//
//            myContactsList.setAdapter(adapter);
//            adapter.startListening();
//        }
//
//
//    }
//
//
//    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
//        TextView userName, userStatus;
//        CircleImageView profileImage;
//        ImageView onlineIcon;
//
//
//        public ContactsViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            userName = itemView.findViewById(R.id.txtUserName);
//            userStatus = itemView.findViewById(R.id.txtUserStatus);
//
//
//        }
//    }
}