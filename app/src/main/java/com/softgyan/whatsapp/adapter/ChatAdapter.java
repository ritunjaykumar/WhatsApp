package com.softgyan.whatsapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.models.Chats;
import com.softgyan.whatsapp.utils.variables.MessageType;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    private List<Chats> chatsList;
    private final Context mContext;
    public static final int TEXT_TYPE = 1;
    public static final int IMAGE_TYPE = 2;
    private final FirebaseUser authUser;
    private int itemPosition = -1;

    public ChatAdapter(List<Chats> chatsList, Context mContext) {
        this.chatsList = chatsList;
        this.mContext = mContext;
        authUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setChatsList(ArrayList<Chats> chatsList) {
        this.chatsList = chatsList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        String viewType = chatsList.get(position).getType();
        itemPosition = position;
        switch (viewType) {
            case MessageType.TEXT:
                return TEXT_TYPE;
            case MessageType.IMAGE:
                return IMAGE_TYPE;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case TEXT_TYPE:
                if (chatsList.get(itemPosition).getSender().equals(authUser.getUid())) {
                    view = layoutInflater.inflate(R.layout.layout_chat_item_right, parent, false);
                } else {
                    view = layoutInflater.inflate(R.layout.layout_chat_item_left, parent, false);
                }
                return new TextMessage(view);

            case IMAGE_TYPE:
                if (chatsList.get(itemPosition).getSender().equals(authUser.getUid())) {
                    view = layoutInflater.inflate(R.layout.layout_chat_image_right, parent, false);
                } else {
                    view = layoutInflater.inflate(R.layout.layout_chat_image_left, parent, false);
                }
                return new ImageMessage(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String viewType = chatsList.get(position).getType();
        Log.d("my_tag", viewType);
        switch (viewType) {
            case MessageType.TEXT:
                ((TextMessage) holder).setTextMessage(chatsList.get(position));
            case MessageType.IMAGE:
                if(holder instanceof ImageMessage){
                    ((ImageMessage) holder).setImageView(chatsList.get(position), mContext);
                }else {
                    ((TextMessage) holder).setTextMessage(chatsList.get(position));
                }
        }
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }


    public static class TextMessage extends RecyclerView.ViewHolder {
        private final TextView tvTextMessage;

        public TextMessage(@NonNull View itemView) {
            super(itemView);
            tvTextMessage = itemView.findViewById(R.id.tv_chat);
        }

        private void setTextMessage(Chats chats) {
            tvTextMessage.setText(chats.getTextMessage());
        }
    }

    public static class ImageMessage extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ImageMessage(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image_chat);
        }

        private void setImageView(Chats chats, Context context) {
            Glide.with(context).load(chats.getImageUrl()).into(imageView);
        }
    }
}
