package com.softgyan.whatsapp.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.adapter.ChatListAdapter;
import com.softgyan.whatsapp.databinding.FragmentChatBinding;
import com.softgyan.whatsapp.models.ChatList;
import com.softgyan.whatsapp.utils.variables.Var;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatFragment() {
        // Required empty public constructor
    }

    private FragmentChatBinding binding;
    private static ChatFragment chatFragment;
    private List<ChatList> chatLists;
    private ChatListAdapter chatListAdapter;
    private FirebaseUser authUser;
    private DatabaseReference dbReference;
    private ArrayList<String> allUserId;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference();
        chatLists = new ArrayList<>();
        allUserId = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatLists);
        binding.rvChatList.setAdapter(chatListAdapter);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ContactActivity.class));
            }
        });

        getChatList();
    }

    private void getChatList() {
        dbReference.child("ChatList").child(authUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatLists.clear();
                allUserId.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String userId = ds.child("chat_id").getValue().toString();
                    allUserId.add(userId);

                }
                getUserData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // getUserInfo
    private void getUserData() {
        for (String user_id : allUserId) {
            FirebaseFirestore.getInstance().collection("Users")
                    .document(user_id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {

                                ChatList chatList = new ChatList(
                                        documentSnapshot.getString(Var.USER_ID),
                                        documentSnapshot.getString(Var.USER_NAME),
                                        documentSnapshot.getString(Var.STATUS),
                                        documentSnapshot.getString(Var.USER_ID),
                                        documentSnapshot.getString(Var.IMAGE_PROFILE)
                                );
                                chatLists.add(chatList);
                                if(chatListAdapter != null){
                                    chatListAdapter.notifyDataSetChanged();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        }
    }
}
