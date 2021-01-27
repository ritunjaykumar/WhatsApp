package com.softgyan.whatsapp.widgets.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.softgyan.whatsapp.BuildConfig;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.utils.UserDetails;
import com.softgyan.whatsapp.widgets.activity.AddStatusImageActivity;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class StatusFragment extends Fragment {

    private static StatusFragment statusFragment;
    private ShapeableImageView sivProfile;
    private TextView tvUserName;
    private FloatingActionButton fabNewStatus;

    private Uri imageUri;

    private StatusFragment() {
    }


    public static StatusFragment getInstance() {
        if (statusFragment == null) {
            statusFragment = new StatusFragment();
        }
        return statusFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        sivProfile = view.findViewById(R.id.siv_profile);
        tvUserName = view.findViewById(R.id.tv_user_name);
        fabNewStatus = view.findViewById(R.id.fab_new_status);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabNewStatus.setOnClickListener(v -> {
            checkCameraPermission();
        });
        setStatusData();
    }

    private void setStatusData() {
        UserDetails user = UserDetails.getInstance(getContext());
        if (user == null)
            return;
        tvUserName.setText(user.getUserName());
        Glide.with(getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_place_holder).into(sivProfile);
    }

    //working on camera
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 111);
        } else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 222);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String uniqueId = UUID.randomUUID().toString().substring(0, 15);
        String fileName = uniqueId + ".jpg";
        try {
            File file = File.createTempFile(uniqueId, ".jpg", getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", fileName);
            startActivityForResult(intent, 444);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 444 && resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {
                startActivity(new Intent(getContext(), AddStatusImageActivity.class)
                        .putExtra("IMAGE_URI", String.valueOf(imageUri)));
            }

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}