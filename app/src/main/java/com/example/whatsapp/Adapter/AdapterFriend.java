package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.GroupChatActivity;
import com.example.whatsapp.Model.UserProfile;
import com.example.whatsapp.ProfileActivity;
import com.example.whatsapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFriend extends RecyclerView.Adapter<AdapterFriend.ViewHolder> {
    Context context;
    List<UserProfile> userProfileList;

    public AdapterFriend(Context context, List<UserProfile> userProfileList) {
        this.context = context;
        this.userProfileList = userProfileList;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserProfile userProfile = userProfileList.get(position);
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
    public int getItemCount() {

        return userProfileList.size();
    }


}
