package com.softgyan.whatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.models.Chats;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<Chats> chatsList;
    private final Context mContext;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final FirebaseUser authUser ;

    public ChatAdapter(List<Chats> chatsList, Context mContext) {
        this.chatsList = chatsList;
        this.mContext = mContext;
        authUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatsList.get(position).getSender().equals(authUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if (viewType == MSG_TYPE_RIGHT) {
            view = layoutInflater.inflate(R.layout.layout_chat_item_right, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.layout_chat_item_left, parent, false);
        }
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.messageBind(chatsList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTextMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTextMessage = itemView.findViewById(R.id.tv_chat);
        }
        private void messageBind(Chats chats){
            tvTextMessage.setText(chats.getTextMessage());
        }
    }
}
