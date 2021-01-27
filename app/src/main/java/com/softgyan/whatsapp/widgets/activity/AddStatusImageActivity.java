package com.softgyan.whatsapp.widgets.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityAddStatusImageBinding;
import com.softgyan.whatsapp.managers.ChatServices;
import com.softgyan.whatsapp.models.StatusModel;
import com.softgyan.whatsapp.servieces.FirebaseServices;
import com.softgyan.whatsapp.utils.UserDetails;

import java.util.Objects;
import java.util.UUID;

public class AddStatusImageActivity extends AppCompatActivity {

    private ActivityAddStatusImageBinding mBinding;
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_status_image);
        setSupportActionBar(mBinding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            imageUri = getIntent().getStringExtra("IMAGE_URI");
        }

        if (imageUri != null) {
            Glide.with(this).load(imageUri).placeholder(R.drawable.ic_place_holder).into(mBinding.ivDisplay);
        }
        Glide.with(this).load(UserDetails.getInstance(this).getImageProfile()).placeholder(R.drawable.ic_place_holder).into(mBinding.ivDisplay);


        mBinding.fabSend.setOnClickListener(v -> {
            closeKeyboard();
            uploadStatus();
        });
    }

    private void uploadStatus() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.fabSend.setVisibility(View.GONE);

        String description = !TextUtils.isEmpty(mBinding.tvDescription.getText().toString().trim())
                ? mBinding.tvDescription.getText().toString().trim() : "";
        new FirebaseServices(AddStatusImageActivity.this).uploadImageToFirebase(Uri.parse(imageUri),
                new FirebaseServices.OnCallBack() {

                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        StatusModel statusModel = new StatusModel();
                        statusModel.setId(UUID.randomUUID().toString());
                        statusModel.setCreateDate(ChatServices.getCurrent());
                        statusModel.setImageStatus(imageUrl);
                        statusModel.setUserId(FirebaseAuth.getInstance().getUid());
                        statusModel.setViewCount(0);
                        statusModel.setTextStatus(description);

                        new FirebaseServices(AddStatusImageActivity.this).addNewStatus(statusModel,
                                new FirebaseServices.OnAddStatusCallBack() {
                                    @Override
                                    public void success() {
                                        Toast.makeText(AddStatusImageActivity.this, "success", Toast.LENGTH_SHORT).show();
                                        mBinding.progressBar.setVisibility(View.GONE);
                                        mBinding.fabSend.setVisibility(View.VISIBLE);
                                        finish();
                                    }

                                    @Override
                                    public void failed(Exception e) {
                                        Toast.makeText(AddStatusImageActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                        Log.d("my_tag", "failed: " + e.getMessage());
                                        mBinding.progressBar.setVisibility(View.GONE);
                                        mBinding.fabSend.setVisibility(View.VISIBLE);
                                        finish();
                                    }
                                });

                    }

                    @Override
                    public void onUploadFailed(Exception e) {
                        Toast.makeText(AddStatusImageActivity.this, "fialed", Toast.LENGTH_SHORT).show();
                        Log.d("my_tag", "failed: " + e.getMessage());
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.fabSend.setVisibility(View.VISIBLE);
                        finish();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive(mBinding.tvDescription)) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}