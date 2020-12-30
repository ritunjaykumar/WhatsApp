package com.softgyan.whatsapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.models.CallList;

import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.ChatViewHolder> {
    private List<CallList> callLists;

    public CallListAdapter(List<CallList> callLists) {
        this.callLists = callLists;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_call_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        CallList callList = callLists.get(position);
        holder.tvUserName.setText(callList.getUserName());
        holder.tvDate.setText(callList.getDate());
        holder.ivCallType.setImageResource(callList.getCallType());
        Glide.with(holder.itemView.getContext())
                .load(callList.getUrlProfile())
                .placeholder(R.drawable.ic_place_holder)
                .into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return callLists.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView ivProfile;
        private final ImageView  ivCallType, ivCall;
        private final TextView tvUserName, tvDate;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivCallType = itemView.findViewById(R.id.im_call_type);
            ivCall = itemView.findViewById(R.id.iv_call);
        }
    }
}
