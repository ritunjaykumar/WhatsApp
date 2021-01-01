package com.softgyan.whatsapp.widgets;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityChatBinding;
import com.softgyan.whatsapp.models.Chats;
import com.softgyan.whatsapp.utils.variables.MessageType;
import com.softgyan.whatsapp.utils.variables.Var;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding mBinding;
    private FirebaseUser authUser;
    private DatabaseReference dbReference;
    private String receiverId;

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
                Log.d("my_tag", "start : " + start + " before : " + before + " count : " + count);
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
            }

        });
    }

    private void sendTextMessage(String message){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = dateFormat.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentTime = sdf.format(currentDateTime);
        Chats chats = new Chats(
                today+", "+currentTime,
                message,
                MessageType.TEXT,
                authUser.getUid(),
                ""
        );
        dbReference.child("Chats").setValue(chats);

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

    private void setData(Bundle bundle) {
        mBinding.tvUserName.setText(bundle.getString(Var.USER_NAME));
        receiverId = bundle.getString(Var.USER_ID,null);
        Glide.with(this)
                .load(bundle.getString(Var.IMAGE_PROFILE))
                .placeholder(R.drawable.ic_place_holder)
                .into(mBinding.sivProfile);
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