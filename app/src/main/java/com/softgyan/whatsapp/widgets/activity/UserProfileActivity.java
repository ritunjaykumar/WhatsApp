package com.softgyan.whatsapp.widgets.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding mBinding;
    private String receiverId;
    private String receiverProfile;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        setSupportActionBar(mBinding.toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            receiverId = getIntent().getStringExtra(ChatActivity.RECEIVER_ID);
            receiverProfile = getIntent().getStringExtra(ChatActivity.RECEIVER_PROFILE);
            receiverName = getIntent().getStringExtra(ChatActivity.RECEIVER_NAME);
            setReceiverProfile();
        }
    }


    private void setReceiverProfile() {
        mBinding.toolBar.setTitle(receiverName);
        Glide.with(this).load(receiverProfile).placeholder(R.drawable.ic_place_holder).into(mBinding.ivProfile);
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