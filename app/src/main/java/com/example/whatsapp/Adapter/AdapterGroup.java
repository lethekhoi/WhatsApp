package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.GroupChatActivity;
import com.example.whatsapp.Model.GroupDetail;
import com.example.whatsapp.R;

import java.util.List;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.ViewHolder> {
    Context context;
    List<GroupDetail> groupDetails;

    public AdapterGroup(Context context, List<GroupDetail> groupDetails) {
        this.context = context;
        this.groupDetails = groupDetails;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imgGroup;
        LinearLayout lnGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txtNameGroup);
            imgGroup = itemView.findViewById(R.id.imgGroup);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent iGroupChat = new Intent(context, GroupChatActivity.class);
                    iGroupChat.putExtra("groupdetail", groupDetails.get(getPosition()));
                    context.startActivity(iGroupChat);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_layout_recycler_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        GroupDetail groupDetail = groupDetails.get(position);
        holder.textView.setText(groupDetail.getName());


    }


    @Override
    public int getItemCount() {
        return groupDetails.size();
    }

}
