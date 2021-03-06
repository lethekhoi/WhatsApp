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

import com.example.whatsapp.ChatActivity;
import com.example.whatsapp.Model.UserProfile;
import com.example.whatsapp.Model.userState;
import com.example.whatsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.ViewHolder> {
    private DatabaseReference mDatabaseUser;
    Context context;
    List<String> userIDList;

    public AdapterChatList(Context context, List<String> userIDList) {
        this.context = context;
        this.userIDList = userIDList;
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @NonNull
    @Override
    public AdapterChatList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_layout_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterChatList.ViewHolder holder, final int position) {
        mDatabaseUser.child(userIDList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                final userState userState = userProfile.getUserState();
                if (userState != null) {
                    String date = userState.getDay();
                    String time = userState.getTime();
                    String state = userState.getState();


                    if (state.equals("online")) {
                        holder.imgOnline.setVisibility(View.VISIBLE);
                        holder.txtStatus.setVisibility(View.INVISIBLE);
                    } else if (state.equals("offline")){
                        holder.imgOnline.setVisibility(View.INVISIBLE);
                        holder.txtStatus.setVisibility(View.VISIBLE);
                        holder.txtStatus.setText("Last seen :" + date + " " + time);
                    }
                }



                holder.txtName.setText(userProfile.getName());
                if(userProfile.getImage()==null|userProfile.getImage()=="")

                {
                    holder.imgUserProfil.setImageResource(R.drawable.default_user);
                } else

                {
                    Picasso.get().load(userProfile.getImage()).into(holder.imgUserProfil);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick (View view){

                        Intent iProfile = new Intent(context, ChatActivity.class);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtStatus;
        ImageView imgUserProfil, imgOnline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtUserName);
            txtStatus = itemView.findViewById(R.id.txtUserStatus);
            imgUserProfil = itemView.findViewById(R.id.user_profile_image);
            imgOnline = itemView.findViewById(R.id.imgonline);
        }
    }
}
