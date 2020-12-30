package com.softgyan.whatsapp.widgets;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityChatBinding;
import com.softgyan.whatsapp.utils.variables.Var;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding mBinding;

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
        mBinding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("my_tag", "start : "+start + " before : "+before+ " count : "+count);
                if(s.length() == 0){
                    mBinding.ibCamera.setVisibility(View.VISIBLE);
                    mBinding.fabMic.setVisibility(View.VISIBLE);
                    mBinding.fabSend.setVisibility(View.GONE);
                }else {
                    mBinding.ibCamera.setVisibility(View.GONE);
                    mBinding.fabMic.setVisibility(View.GONE);
                    mBinding.fabSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setData(Bundle bundle) {
        mBinding.tvUserName.setText(bundle.getString(Var.USER_NAME));
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