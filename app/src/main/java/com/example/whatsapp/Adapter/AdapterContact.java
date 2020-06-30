package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Model.UserProfile;
import com.example.whatsapp.ProfileActivity;
import com.example.whatsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ViewHolder> {
    Context context;
    List<String> userIDList;
    private DatabaseReference mDatabaseUser;

    public AdapterContact(Context context, List<String> userIDList) {
        this.context = context;
        this.userIDList = userIDList;
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtStatus;
        ImageView imgUserProfil;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtUserName);
            txtStatus = itemView.findViewById(R.id.txtUserStatus);
            imgUserProfil = itemView.findViewById(R.id.user_profile_image);


        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_layout_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        mDatabaseUser.child(userIDList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                holder.txtName.setText(userProfile.getName());
                holder.txtStatus.setText(userProfile.getStatus());
                if (userProfile.getImage() == null | userProfile.getImage() == "") {
                    holder.imgUserProfil.setImageResource(R.drawable.default_user);
                } else {
                    Picasso.get().load(userProfile.getImage()).into(holder.imgUserProfil);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id = userProfile.getUid();
                        Intent iProfile = new Intent(context, ProfileActivity.class);
                        iProfile.putExtra("userprofile", userProfile);
                        context.startActivity(iProfile);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {

        return userIDList.size();
    }


}
