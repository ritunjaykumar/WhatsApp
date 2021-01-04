package com.softgyan.whatsapp.widgets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.adapter.ChatAdapter;
import com.softgyan.whatsapp.databinding.ActivityChatBinding;
import com.softgyan.whatsapp.models.Chats;
import com.softgyan.whatsapp.utils.variables.MessageType;
import com.softgyan.whatsapp.utils.variables.Var;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * ** bundle data of receiver contact **
 * user_name
 * user_id
 * image_profile
 */
public class ChatActivity extends AppCompatActivity {
    public static final String RECEIVER_ID = "receiver_id";
    public static final String RECEIVER_PROFILE = "receiver_profile";
    public static final String RECEIVER_NAME = "receiver_name";
    private ActivityChatBinding mBinding;
    private FirebaseUser authUser;
    private DatabaseReference dbReference;
    private String receiverId;
    //    private String senderId;
    private ChatAdapter chatAdapter;
    private List<Chats> chatsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        setSupportActionBar(mBinding.toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getBundleExtra(Var.USERS);
            setData(bundle);
        }


        authUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference();

        mBinding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("my_tag", "start : " + start + " before : " + before + " count : " + count);
                if (s.length() == 0) {
                    mBinding.ibCamera.setVisibility(View.VISIBLE);
                    mBinding.fabMic.setVisibility(View.VISIBLE);
                    mBinding.fabSend.setVisibility(View.GONE);
                } else {
                    mBinding.ibCamera.setVisibility(View.GONE);
                    mBinding.fabMic.setVisibility(View.GONE);
                    mBinding.fabSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //blank
            }
        });
        mBinding.fabSend.setOnClickListener(view -> {
            String message = mBinding.etMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                sendTextMessage(message);
                mBinding.etMessage.setText("");
            }

        });
        chatsList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        mBinding.rvChat.setLayoutManager(layoutManager);


        readChat();
    }

    private void sendTextMessage(String message) {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = dateFormat.format(date);
        //todo we are getting error here
//        Calendar currentDateTime = Calendar.getInstance();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentTime = "9:10 am"; //sdf.format(currentDateTime.getTime());

        Chats chats = new Chats(
                today + ", " + currentTime,
                message,
                MessageType.TEXT,
                authUser.getUid(),
                receiverId
        );

        //send message
        dbReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("my_tag", "onSuccess : message sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("my_tag", "onFailure : message failed " + e.getMessage());
            }
        });


        //add to sender chat list
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(authUser.getUid()).child(receiverId);
        chatRef1.child("chat_id").setValue(receiverId);

        //add to receiver chat list
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(receiverId).child(authUser.getUid());
        chatRef2.child("chat_id").setValue(authUser.getUid());


    }

    private void readChat() {
        try {

            DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
            dbReference.child("Chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatsList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Chats ch = ds.getValue(Chats.class);

                        if (ch != null && ch.getSender().equals(authUser.getUid()) && ch.getReceiver().equals(receiverId)
                            || Objects.requireNonNull(ch).getReceiver().equals(authUser.getUid()) && ch.getSender().equals(receiverId)) {
//                        if (receiverId.equals(ch.getSender()) || authUser.getUid().equals(ch.getSender())) {
                            chatsList.add(ch);
                        }
                    }
                    if (chatAdapter != null) {
                        chatAdapter.notifyDataSetChanged();
                        mBinding.rvChat.scrollToPosition(chatsList.size()-1);
                    } else {
                        chatAdapter = new ChatAdapter(chatsList, ChatActivity.this);
                        mBinding.rvChat.setAdapter(chatAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "read_chat : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(Bundle bundle) {
        mBinding.tvUserName.setText(bundle.getString(Var.USER_NAME));
        receiverId = bundle.getString(Var.USER_ID, null);
        String receiverProfile = bundle.getString(Var.IMAGE_PROFILE);
        String receiverName = bundle.getString(Var.USER_NAME);
        Glide.with(this)
                .load(receiverProfile)
                .placeholder(R.drawable.ic_place_holder)
                .into(mBinding.sivProfile);
        mBinding.llProfileContainer.setOnClickListener(v ->{
            Intent intent =new Intent(ChatActivity.this, UserProfileActivity.class);
            intent.putExtra(RECEIVER_ID,receiverId);
            intent.putExtra(RECEIVER_PROFILE, receiverProfile );
            intent.putExtra(RECEIVER_NAME, receiverName );
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}