package com.softgyan.whatsapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.models.Chats;
import com.softgyan.whatsapp.tools.AudioService;
import com.softgyan.whatsapp.utils.common.Common;
import com.softgyan.whatsapp.utils.variables.MessageType;
import com.softgyan.whatsapp.widgets.activity.ViewImageActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    private List<Chats> chatsList;
    private final Context mContext;
    public static final int TEXT_TYPE = 1;
    public static final int IMAGE_TYPE = 2;
    public static final int VOICE_TYPE = 3;
    private final FirebaseUser authUser;
    private int itemPosition = -1;
    private final AudioService audioService;
    private OnChatAdapterCallback callback;

    public ChatAdapter(List<Chats> chatsList, Context mContext, OnChatAdapterCallback callback) {
        this.chatsList = chatsList;
        this.mContext = mContext;
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        this.audioService = new AudioService();
        this.callback = callback;
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
            case MessageType.VOICE:
                return VOICE_TYPE;
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

            case VOICE_TYPE:
                if (chatsList.get(itemPosition).getSender().equals(authUser.getUid())) {
                    view = layoutInflater.inflate(R.layout.layout_chat_voice_right, parent, false);
                } else {
                    view = layoutInflater.inflate(R.layout.layout_chat_voice_left, parent, false);
                }
                return new VoiceMessage(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String viewType = chatsList.get(position).getType();
        switch (viewType) {
            case MessageType.TEXT:
                if (holder instanceof TextMessage) {
                    ((TextMessage) holder).setTextMessage(chatsList.get(position));
                }
            case MessageType.IMAGE:
                if (holder instanceof ImageMessage) {
                    ((ImageMessage) holder).setImageView(chatsList.get(position), mContext);
                }
            case MessageType.VOICE:
                if (holder instanceof VoiceMessage) {
                    ((VoiceMessage) holder).setVoiceData(chatsList.get(position));
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
            itemView.setOnClickListener(view -> {
                imageView.invalidate();
                Drawable dr = imageView.getDrawable();
                Common.BIT_MAP = ((BitmapDrawable) dr.getCurrent()).getBitmap();
                ActivityOptionsCompat aoCompact = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity) itemView.getContext(), imageView, "image");
                Intent intent = new Intent(itemView.getContext(), ViewImageActivity.class);
                itemView.getContext().startActivity(intent, aoCompact.toBundle());

            });
        }
    }

    public class VoiceMessage extends RecyclerView.ViewHolder {
        private final ImageButton ibPlay;
        private final Chronometer chronometer;
        private final ImageButton ibPause;

        public VoiceMessage(@NonNull View itemView) {
            super(itemView);
            ibPlay = itemView.findViewById(R.id.ib_play);
            ibPause = itemView.findViewById(R.id.ib_pause);
            chronometer = itemView.findViewById(R.id.cm_count_down);
        }

        private void setVoiceData(Chats chats) {
            ibPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "waiting for downloading", Toast.LENGTH_LONG).show();
                    setLayoutGone(ibPlay);
                    setLayoutVisible(ibPause);
                    audioService.playAudioFromUrl(chats.getImageUrl(), new AudioService.OnAudioServiceCallBack() {
                        @Override
                        public void onFinish() {
                            setLayoutGone(ibPause);
                            setLayoutVisible(ibPlay);
                        }

                        @Override
                        public void onStart(MediaPlayer mediaPlayer) {
                            callback.onGetAudio(mediaPlayer);
                        }
                    });
                }
            });
        }

        private void setLayoutVisible(View... views) {
            for (View view : views) {
                view.setVisibility(View.VISIBLE);
            }
        }

        private void setLayoutGone(View... views) {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }
    }


    public interface OnChatAdapterCallback {
        void onGetAudio(MediaPlayer mediaPlayer);
    }
}
