package com.softgyan.whatsapp.widgets;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivitySetUserInfoBinding;
import com.softgyan.whatsapp.models.Users;
import com.softgyan.whatsapp.utils.UserDetails;
import com.softgyan.whatsapp.utils.variables.Var;

import java.io.IOException;
import java.util.Objects;

public class SetUserInfoActivity extends AppCompatActivity {
    private static final int READ_STORAGE_CODE = 101;
    private ActivitySetUserInfoBinding mBind;

    private ProgressDialog progressDialog;
    private String downloadUrl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_set_user_info);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);
        mBind.btnNext.setOnClickListener(btnView -> {
            updateProfile();
        });
        mBind.ivProfileImage.setOnClickListener(sivView -> {
            checkPermissionFor();
        });
        getImageUrl();

    }

    private void updateProfile() {
        String userName = mBind.etUserName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "please enter valid name", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        if (authUser != null) {
            String userId = authUser.getUid();
            Users users = new Users(userId, userName, authUser.getPhoneNumber(),
                    downloadUrl, "", "", "",
                    "", "Hey, I am using WhatsApp", "");

            db.collection("Users")
                    .document(authUser.getUid())
                    .set(users)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SetUserInfoActivity.this, "update successful", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        UserDetails.getInstance(SetUserInfoActivity.this).setUserInfo(userName);
                        UserDetails.getInstance(SetUserInfoActivity.this).setUserNameSet(true);
                        Intent intent = new Intent(SetUserInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SetUserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
        } else {
            Toast.makeText(this, "something error", Toast.LENGTH_SHORT).show();

        }
    }


    private void checkPermissionFor() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            getImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_CODE);
        }
    }

    private void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), ProfileActivity.GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImage();
        } else {
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProfileActivity.GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(final Uri imageUri) {
        if (imageUri != null) {
            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("ImagesProfile/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageReference.putFile(imageUri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadUri = uriTask.getResult();
                            progressDialog.dismiss();
                            downloadUrl = String.valueOf(downloadUri);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                mBind.ivProfileImage.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //todo set image when image uploaded
                            UserDetails.getInstance(SetUserInfoActivity.this).setImageProfile(downloadUrl);
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(SetUserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void getImageUrl() {
        progressDialog.show();
        FirebaseFirestore.getInstance().collection(Var.USERS)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            String userName = documentSnapshot.getString(Var.USER_NAME);
                            String status = documentSnapshot.getString(Var.STATUS);
                            downloadUrl = documentSnapshot.getString(Var.IMAGE_PROFILE);
                            setDat(userName, status);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void setDat(String userName, String status) {
        UserDetails details = UserDetails.getInstance(SetUserInfoActivity.this);
        details.setUserInfo(userName);
        details.setImageProfile(downloadUrl);
        details.setStatus(status);
        details.setUserNameSet(true);
        mBind.etUserName.setText(userName);
        Glide.with(SetUserInfoActivity.this)
                .load(downloadUrl)
                .placeholder(R.drawable.ic_place_holder)
                .into(mBind.ivProfileImage);

    }

}