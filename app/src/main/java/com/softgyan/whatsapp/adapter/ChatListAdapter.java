package com.softgyan.whatsapp.adapter;

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
import com.softgyan.whatsapp.models.ChatList;
import com.softgyan.whatsapp.utils.variables.Var;
import com.softgyan.whatsapp.widgets.activity.ChatActivity;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private final List<ChatList> chatLists ;

    public ChatListAdapter(List<ChatList> chatLists) {
        this.chatLists = chatLists;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatList chatList = chatLists.get(position);
        holder.tvUserName.setText(chatList.getUserName());
        holder.tvDescription.setText(chatList.getDescription());
        holder.tvDate.setText(chatList.getDate());
        Glide.with(holder.itemView.getContext())
                .load(chatList.getUrlProfile())
                .placeholder(R.drawable.ic_place_holder)
                .into(holder.ivProfile);
        holder.itemView.setOnClickListener(View->{
            Intent intent =new Intent(holder.itemView.getContext(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Var.USER_NAME, chatList.getUserName());
            bundle.putString(Var.USER_ID, chatList.getUserId());
            bundle.putString(Var.IMAGE_PROFILE, chatList.getUrlProfile());
            intent.putExtra(Var.USERS,bundle);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView ivProfile;
        private final TextView tvUserName, tvDate, tvDescription;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvDate = itemView.findViewById(R.id.tv_date );
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
