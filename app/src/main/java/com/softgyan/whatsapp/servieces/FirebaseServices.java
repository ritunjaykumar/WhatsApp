package com.softgyan.whatsapp.servieces;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softgyan.whatsapp.utils.variables.Var;

import java.util.HashMap;

public class FirebaseServices {
    private final Context mContext;

    public FirebaseServices(Context mContext) {
        this.mContext = mContext;
    }

    public void uploadImageToFirebase(Uri imageUri, OnCallBack onCallBack) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("ImagesChat/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageReference.putFile(imageUri).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        final String downloadUrl = String.valueOf(downloadUri);
                        onCallBack.onUploadSuccess(downloadUrl);

                    }
                })
                .addOnFailureListener(onCallBack::onUploadFailed);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = mContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public interface OnCallBack{
        void onUploadSuccess(String imageUrl);
        void onUploadFailed(Exception e);
    }
}
