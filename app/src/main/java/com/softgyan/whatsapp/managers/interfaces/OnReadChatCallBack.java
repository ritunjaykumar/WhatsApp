package com.softgyan.whatsapp.managers.interfaces;

import com.softgyan.whatsapp.models.Chats;

import java.util.List;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chats> chatsList);
    void onReadFailed();
}
