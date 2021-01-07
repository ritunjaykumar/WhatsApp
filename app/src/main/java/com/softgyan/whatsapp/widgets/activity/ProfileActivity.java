package com.softgyan.whatsapp.widgets.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.databinding.ActivityProfileBinding;
import com.softgyan.whatsapp.utils.UserDetails;
import com.softgyan.whatsapp.utils.common.Common;
import com.softgyan.whatsapp.utils.database.ServerData;
import com.softgyan.whatsapp.utils.variables.Var;

import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private static final int STORAGE_REQUEST_CODE = 2;
    public static int GALLERY_REQUEST_CODE = 1;

    private ActivityProfileBinding mBinding;
    private FirebaseFirestore db;
    private FirebaseUser authUser;
    private BottomSheetDialog bottomSheetDialog;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        if (authUser != null) {
            getUserData();
        }
        mBinding.fabAddImage.setOnClickListener(v -> {
            showBottomSheetImagePick();
        });
        mBinding.ibUserName.setOnClickListener(view -> showBottomSheetNameUpdate());
        mBinding.sivProfile.setOnClickListener(v -> {
            mBinding.sivProfile.invalidate();
            Drawable dr = mBinding.sivProfile.getDrawable();
            Common.BIT_MAP = ((BitmapDrawable) dr.getCurrent()).getBitmap();
            ActivityOptionsCompat aoCompact = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(ProfileActivity.this, mBinding.sivProfile, "image");
            Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
            startActivity(intent, aoCompact.toBundle());
        });

        mBinding.btnLogout.setOnClickListener(view -> {
            showDialogSignOut();
        });
    }


    private void showBottomSheetNameUpdate() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.layout_name_update);
        EditText etName = bottomSheetDialog.findViewById(R.id.et_user_name);
        bottomSheetDialog.findViewById(R.id.btn_save).setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "incorrect user name", Toast.LENGTH_SHORT).show();
            } else {
                updateName(name);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        bottomSheetDialog.setOnCancelListener(dialog -> bottomSheetDialog = null);
        bottomSheetDialog.show();
    }

    private void updateName(String name) {
        HashMap<String, Object> updateName = new HashMap();
        updateName.put(Var.USER_NAME, name);
        ServerData.getInstance(ProfileActivity.this, new ServerData.ServerDataListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProfileActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
                UserDetails.getInstance(ProfileActivity.this).setUserInfo(name);
                mBinding.tvUserName.setText(name);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).updateProfile(updateName);

    }

    private void showBottomSheetImagePick() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_pic);

        bottomSheetDialog.findViewById(R.id.ll_gallery).setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
            }

            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.findViewById(R.id.ll_camera).setOnClickListener(view -> {

            Toast.makeText(this, "camera under construction", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        bottomSheetDialog.setOnCancelListener(dialog -> bottomSheetDialog = null);
        bottomSheetDialog.show();

    }


    private void getUserData() {
        UserDetails userDetails = UserDetails.getInstance(this);
        String status = userDetails.getStatus();
        String profileUrl = userDetails.getImageProfile();
        String userName = userDetails.getUserName();
        mBinding.tvUserName.setText(userName);
        mBinding.tvPhone.setText(authUser.getPhoneNumber());
        mBinding.tvStatus.setText(status);
        Glide.with(ProfileActivity.this).load(profileUrl).placeholder(R.drawable.ic_place_holder).into(mBinding.sivProfile);
    }


    //    this is for onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                uploadImageToFirebase();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            progressDialog.setMessage("uploading...");
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
                            final String downloadUrl = String.valueOf(downloadUri);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(Var.IMAGE_PROFILE, downloadUrl);

                            //uploading data to server this class is create by myself
                            ServerData.getInstance(ProfileActivity.this, new ServerData.ServerDataListener() {
                                @Override
                                public void onSuccess() {
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                        mBinding.sivProfile.setImageBitmap(bitmap); //todo set image when image uploaded
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    UserDetails.getInstance(ProfileActivity.this).setImageProfile(downloadUrl);
                                    Toast.makeText(ProfileActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(Exception e) {
                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    mBinding.sivProfile.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_place_holder));
                                }
                            }).updateProfile(hashMap);
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }
    }

    private void showDialogSignOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to sign out?");
        builder.setPositiveButton("Sign Out", (dialog, which) -> {
            dialog.dismiss();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, WelcomeActivity.class));
            UserDetails.getInstance(ProfileActivity.this).logout();
            finishAffinity();

//            Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

//todo working with local storage for working fast


//                            FirebaseFirestore.getInstance().collection(Var.USERS)
//                                    .document(authUser.getUid())
//                                    .update(hashMap)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            try {
//                                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                                                mBinding.sivProfile.setImageBitmap(bitmap); //todo set image when image uploaded
//                                            } catch (IOException ex) {
//                                                ex.printStackTrace();
//                                            }
//                                            UserDetails.getInstance(ProfileActivity.this).setImageProfile(downloadUrl);
//                                            Toast.makeText(ProfileActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
//                                            progressDialog.dismiss();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            progressDialog.dismiss();
//                                            //getUserData(); //todo working with local storage
//                                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            mBinding.sivProfile.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_place_holder));
//                                        }
//                                    });