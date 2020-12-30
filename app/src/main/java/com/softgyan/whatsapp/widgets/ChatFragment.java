package com.softgyan.whatsapp.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.adapter.ChatListAdapter;
import com.softgyan.whatsapp.models.ChatList;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatFragment() {
        // Required empty public constructor
    }

    private static ChatFragment chatFragment;
    private List<ChatList> chatLists = new ArrayList();
    private RecyclerView rvChatList;
    private ChatListAdapter chatListAdapter;
    private FloatingActionButton fabCall;

    public static ChatFragment getInstance() {
        if (chatFragment == null) {
            chatFragment = new ChatFragment();
        }
        return chatFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getChatList();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        rvChatList = view.findViewById(R.id.rv_chat_list);
        fabCall = view.findViewById(R.id.fab_action);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatListAdapter = new ChatListAdapter(chatLists);
        rvChatList.setAdapter(chatListAdapter);
        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ContactActivity.class));
            }
        });
    }

    private void getChatList() {
        String img1 ="https://static.toiimg.com/photo/msid-77142971/77142971.jpg";
        String img2 ="https://i.ytimg.com/vi/zCcGRZ9kAhU/maxresdefault.jpg";
        String img3 = "https://i.ytimg.com/vi/csAPrTKNJPY/maxresdefault.jpg";
        String img4 ="https://vignette.wikia.nocookie.net/motu-patlu/images/3/3e/Profile_motu2.png";
        String img5 ="https://vignette.wikia.nocookie.net/jorjorswackyjourney/images/1/18/Motu_patlu.jpg";
        chatLists.add(new ChatList("11", "Ritunjay", "hi", "12/02/2002", img1));
        chatLists.add(new ChatList("11", "Arunjay", "good morning", "10/03/2020", img2));
        chatLists.add(new ChatList("11", "Richa", "good night", "24/03/2002", img3));
        chatLists.add(new ChatList("11", "Renu", "have you dinner", "01/05/2002", img4));
        chatLists.add(new ChatList("11", "Jyoti", "come to my home", "01/01/2002", img5));
    }
}
