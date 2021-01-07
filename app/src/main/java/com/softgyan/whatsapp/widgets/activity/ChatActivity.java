package com.softgyan.whatsapp.widgets.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.adapter.ChatAdapter;
import com.softgyan.whatsapp.databinding.ActivityChatBinding;
import com.softgyan.whatsapp.dialog.DialogReviewSendImage;
import com.softgyan.whatsapp.managers.ChatServices;
import com.softgyan.whatsapp.managers.interfaces.OnReadChatCallBack;
import com.softgyan.whatsapp.models.Chats;
import com.softgyan.whatsapp.servieces.FirebaseServices;
import com.softgyan.whatsapp.utils.variables.Var;

import java.io.IOException;
import java.util.ArrayList;
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
    private static final int GALLERY_REQUEST_CODE = 101;
    private Uri imageUri;

    private ActivityChatBinding mBinding;
    private String receiverId;
    private ArrayList<Chats> chatsList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private boolean isActionShow = false;
    private LinearLayout llGalleryContainer;
    private ChatServices chatServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        setSupportActionBar(mBinding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getBundleExtra(Var.USERS);
            setData(bundle);
        }

        llGalleryContainer = findViewById(R.id.ll_gallery_container);

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
                chatServices.sendTextMessage(message);
                mBinding.etMessage.setText("");
            }

        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        mBinding.rvChat.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(chatsList, this);
        mBinding.rvChat.setAdapter(chatAdapter);
        mBinding.ibAttachment.setOnClickListener(view -> {
            if (isActionShow) {
                llGalleryContainer.setVisibility(View.GONE);
            } else {
                llGalleryContainer.setVisibility(View.VISIBLE);
            }
            isActionShow = !isActionShow;
        });
        findViewById(R.id.ll_gallery).setOnClickListener(view -> {
            openGallery();
            llGalleryContainer.setVisibility(View.GONE);
        });


        readChat();
    }

    private void readChat() {
        chatServices.readChats(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chats> chatsList) {
                chatAdapter.setChatsList((ArrayList<Chats>) chatsList);
                mBinding.rvChat.scrollToPosition(chatsList.size() - 1);
            }

            @Override
            public void onReadFailed() {

            }
        });
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
        mBinding.llProfileContainer.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
            intent.putExtra(RECEIVER_ID, receiverId);
            intent.putExtra(RECEIVER_PROFILE, receiverProfile);
            intent.putExtra(RECEIVER_NAME, receiverName);
            startActivity(intent);
        });
        chatServices = new ChatServices(this, receiverId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    //****************************************open gallery send images*********************************************//
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
//                uploadImageToFirebase();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    reviewImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void reviewImage(Bitmap bitmap) {
        new DialogReviewSendImage(this, bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onButtonSendClick() {
                //to upload image to firebase server
                if (imageUri != null) {
                    ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
                    progressDialog.setMessage("Sending image");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new FirebaseServices(ChatActivity.this)
                            .uploadImageToFirebase(imageUri, new FirebaseServices.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            //send image chat image
                            chatServices.sendImage(imageUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}