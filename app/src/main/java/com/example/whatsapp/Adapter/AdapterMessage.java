package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Model.Message;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.ViewHolder> {
    List<Message> messageList;
    Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public AdapterMessage(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new AdapterMessage.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String current_user_id = mAuth.getCurrentUser().getUid();
        ViewGroup.LayoutParams paramsSender = holder.messageSenderImage.getLayoutParams();
        ViewGroup.LayoutParams paramsReceive = holder.messageReceiveImage.getLayoutParams();
        final Message message = messageList.get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(message.getFrom());

        holder.cardView.setVisibility(View.INVISIBLE);
        holder.receiveImageProfile.setVisibility(View.INVISIBLE);
        holder.receiveMessage.setVisibility(View.INVISIBLE);
        holder.sendMessage.setVisibility(View.INVISIBLE);
        holder.messageReceiveImage.setVisibility(View.INVISIBLE);
        holder.messageReceiveImage.setEnabled(false);
        holder.messageSenderImage.setEnabled(false);
        holder.messageSenderImage.setVisibility(View.INVISIBLE);
        //nếu message là text
        if (message.getType().equals("text")) {

            paramsSender.height = 0;
            paramsSender.width = 0;
            paramsReceive.height = 0;
            paramsReceive.width = 0;
            if (current_user_id.equals(message.getFrom())) {
                holder.sendMessage.setVisibility(View.VISIBLE);
                holder.sendMessage.setBackgroundResource(R.drawable.send_message_layout);
                holder.sendMessage.setText(message.getMessage());
            } else {

                if (position != (messageList.size() - 1)) {
                    if (message.getFrom().equals(messageList.get(position + 1).getFrom())) {
                        holder.receiveImageProfile.setVisibility(View.INVISIBLE);
                        holder.cardView.setVisibility(View.INVISIBLE);
                    } else {
                        holder.receiveImageProfile.setVisibility(View.VISIBLE);
                        holder.cardView.setVisibility(View.VISIBLE);
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {
                                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(holder.receiveImageProfile);
                                } else {
                                    holder.receiveImageProfile.setImageResource(R.drawable.default_user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    holder.receiveImageProfile.setVisibility(View.VISIBLE);
                    holder.cardView.setVisibility(View.VISIBLE);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("image")) {
                                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(holder.receiveImageProfile);
                            } else {
                                holder.receiveImageProfile.setImageResource(R.drawable.default_user);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                holder.receiveMessage.setVisibility(View.VISIBLE);
                holder.receiveMessage.setBackgroundResource(R.drawable.receive_message_layout);
                holder.receiveMessage.setText(message.getMessage());
            }
        } else if (message.getType().equals("image")) {
            if (current_user_id.equals(message.getFrom())) {
                holder.messageSenderImage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(holder.messageSenderImage);

            } else {

//                holder.messageReceiveImage.setVisibility(View.VISIBLE);
//                Picasso.get().load(message.getMessage()).into(holder.messageReceiveImage);

                if (position != (messageList.size() - 1)) {
                    if (message.getFrom().equals(messageList.get(position + 1).getFrom())) {
                        holder.receiveImageProfile.setVisibility(View.INVISIBLE);
                        holder.cardView.setVisibility(View.INVISIBLE);
                    } else {
                        holder.receiveImageProfile.setVisibility(View.VISIBLE);
                        holder.cardView.setVisibility(View.VISIBLE);
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {
                                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(holder.receiveImageProfile);
                                } else {
                                    holder.receiveImageProfile.setImageResource(R.drawable.default_user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    holder.receiveImageProfile.setVisibility(View.VISIBLE);
                    holder.cardView.setVisibility(View.VISIBLE);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("image")) {
                                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(holder.receiveImageProfile);
                            } else {
                                holder.receiveImageProfile.setImageResource(R.drawable.default_user);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                RelativeLayout.LayoutParams parms1 = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();
                parms1.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.message_receive_image_view);
                holder.cardView.setLayoutParams(parms1);
                holder.messageReceiveImage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(holder.messageReceiveImage);


            }


        } else {
            if (current_user_id.equals(message.getFrom())) {

                paramsSender.height = 128;
                paramsSender.width = 128;
                paramsReceive.height = 0;
                paramsReceive.width = 0;
                holder.messageSenderImage.setVisibility(View.VISIBLE);
                holder.messageSenderImage.setBackgroundResource(message.getType().equals("pdf") ? R.drawable.ic_pdf :
                        message.getType().equals("docx") ? R.drawable.ic_docx : R.drawable.file);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        holder.messageSenderImage.getContext().startActivity(intent);
                    }
                });
            } else {
                if (position != (messageList.size() - 1)) {
                    if (message.getFrom().equals(messageList.get(position + 1).getFrom())) {
                        holder.receiveImageProfile.setVisibility(View.INVISIBLE);
                        holder.cardView.setVisibility(View.INVISIBLE);
                    } else {
                        holder.receiveImageProfile.setVisibility(View.VISIBLE);
                        holder.cardView.setVisibility(View.VISIBLE);
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {
                                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(holder.receiveImageProfile);
                                } else {
                                    holder.receiveImageProfile.setImageResource(R.drawable.default_user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    holder.receiveImageProfile.setVisibility(View.VISIBLE);
                    holder.cardView.setVisibility(View.VISIBLE);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("image")) {
                                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(holder.receiveImageProfile);
                            } else {
                                holder.receiveImageProfile.setImageResource(R.drawable.default_user);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                paramsReceive.height = 128;
                paramsReceive.width = 128;
                paramsSender.height = 0;
                paramsSender.width = 0;
                holder.messageReceiveImage.setVisibility(View.VISIBLE);
                holder.messageReceiveImage.setBackgroundResource(message.getType().equals("pdf") ? R.drawable.ic_pdf :
                        message.getType().equals("docx") ? R.drawable.ic_docx : R.drawable.file);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        holder.messageSenderImage.getContext().startActivity(intent);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sendMessage, receiveMessage;
        ImageView receiveImageProfile, messageSenderImage, messageReceiveImage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessage = itemView.findViewById(R.id.send_message_text);
            receiveMessage = itemView.findViewById(R.id.receive_message_text);
            receiveImageProfile = itemView.findViewById(R.id.message_profile_image);
            messageSenderImage = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiveImage = itemView.findViewById(R.id.message_receive_image_view);
            cardView = itemView.findViewById(R.id.imgcardview);
        }
    }
}
