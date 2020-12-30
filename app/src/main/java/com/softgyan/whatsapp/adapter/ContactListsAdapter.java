package com.softgyan.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.models.Users;
import com.softgyan.whatsapp.utils.variables.Var;
import com.softgyan.whatsapp.widgets.ChatActivity;

import java.util.List;

public class ContactListsAdapter extends RecyclerView.Adapter<ContactListsAdapter.ViewHolder> {
    private final List<Users> usersList;
    private final Context mContext;

    public ContactListsAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_contact_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.tvUserName.setText(users.getUserName());
        holder.tvStatus.setText(users.getStatus());
        Glide.with(mContext).load(users.getImageProfile())
                .placeholder(R.drawable.ic_place_holder)
                .into(holder.spiProfile);

        holder.itemView.setOnClickListener(view->{
            Intent intent =new Intent(mContext, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Var.USER_NAME, users.getUserName());
            bundle.putString(Var.USER_ID, users.getUserId());
            bundle.putString(Var.IMAGE_PROFILE, users.getImageProfile());
            intent.putExtra(Var.USERS,bundle);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder{
        private final ShapeableImageView spiProfile;
        private final TextView tvUserName;
        private final TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            spiProfile = itemView.findViewById(R.id.siv_profile);
        }
    }
}
