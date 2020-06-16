package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Model.UserProfile;
import com.example.whatsapp.ProfileActivity;
import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AdapterRequest extends RecyclerView.Adapter<AdapterRequest.ViewHolder> {
    Context context;
    List<UserProfile> userProfileList;
    String current_user_id;

    public AdapterRequest(Context context, List<UserProfile> userProfileList, String current_user_id) {
        this.context = context;
        this.userProfileList = userProfileList;
        this.current_user_id = current_user_id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtStatus;
        ImageView imgUserProfil;
        Button btnAcceptRequest, btnCancelRequest;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtUserName);
            txtStatus = itemView.findViewById(R.id.txtUserStatus);
            imgUserProfil = itemView.findViewById(R.id.user_profile_image);
            btnAcceptRequest = itemView.findViewById(R.id.btnAcceptRequest);
            btnCancelRequest = itemView.findViewById(R.id.btnCancelRequest);

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final UserProfile userProfile = userProfileList.get(position);
        final String sender_user_id = userProfile.getUid().toString();
        final DatabaseReference mContactDatabase = FirebaseDatabase.getInstance().getReference().child("Contacts");
        final DatabaseReference mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        holder.btnAcceptRequest.setVisibility(View.VISIBLE);
        holder.btnCancelRequest.setVisibility(View.VISIBLE);
        holder.btnAcceptRequest.setEnabled(true);
        holder.btnCancelRequest.setEnabled(true);
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

        holder.btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContactDatabase.child(sender_user_id).child(current_user_id)
                        .child("Contacts").setValue("Saved")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mContactDatabase.child(current_user_id).child(sender_user_id)
                                            .child("Contacts").setValue("Saved")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mChatDatabase.child(sender_user_id).child(current_user_id)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            mChatDatabase.child(current_user_id).child(sender_user_id)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            Toast.makeText(context, "New Contacts Saved", Toast.LENGTH_SHORT).show();
                                                                                            holder.btnAcceptRequest.setVisibility(View.INVISIBLE);
                                                                                            holder.btnCancelRequest.setVisibility(View.INVISIBLE);
                                                                                            notifyDataSetChanged();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });

        holder.btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChatDatabase.child(sender_user_id).child(current_user_id)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mChatDatabase.child(current_user_id).child(sender_user_id)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Cancel request", Toast.LENGTH_SHORT).show();
                                                        holder.btnAcceptRequest.setVisibility(View.INVISIBLE);
                                                        holder.btnCancelRequest.setVisibility(View.INVISIBLE);
                                                        notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                }
                            }
                        });

            }
        });

    }

    @Override
    public int getItemCount() {

        return userProfileList.size();
    }


}
