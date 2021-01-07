package com.softgyan.whatsapp.utils.database;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softgyan.whatsapp.utils.variables.Var;

import java.util.HashMap;

public class ServerData {
    private final FirebaseUser authUser;
    private final ServerDataListener serverDataListener;
    private final ProgressDialog progressDialog;
    private ServerData(Context context, ServerDataListener serverData){
        serverDataListener = serverData;
        progressDialog = new ProgressDialog(context);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");
    }
    public static ServerData getInstance( Context context, ServerDataListener serverDataListener){
        return new ServerData(context, serverDataListener);
    }
    public void updateProfile(HashMap<String, Object> userData){
        progressDialog.show();
        FirebaseFirestore.getInstance().collection(Var.USERS)
                .document(authUser.getUid())
                .update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        serverDataListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        serverDataListener.onFailed(e);
                    }
                });

    }

    public interface ServerDataListener{
        void onSuccess();
        void onFailed(Exception e);
    }
}
