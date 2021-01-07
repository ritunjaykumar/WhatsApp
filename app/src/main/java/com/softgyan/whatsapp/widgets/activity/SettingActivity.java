package com.softgyan.whatsapp.widgets.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivitySettingBinding;
import com.softgyan.whatsapp.utils.UserDetails;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUserInfo() {
        UserDetails userDetails = UserDetails.getInstance(this);
        String userName = userDetails.getUserName();
        String profileUrl = userDetails.getImageProfile();
        String status = userDetails.getStatus();
        mBinding.tvUserName.setText(userName);
        mBinding.tvStatus.setText(status);
        Glide.with(SettingActivity.this).load(profileUrl).placeholder(R.drawable.ic_place_holder).into(mBinding.sivUserProfile);
        mBinding.llProfile.setOnClickListener(v -> startActivity(new Intent(SettingActivity.this, ProfileActivity.class)));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserInfo();
    }
}