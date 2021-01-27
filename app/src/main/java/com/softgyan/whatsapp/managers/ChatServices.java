package com.softgyan.whatsapp.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softgyan.whatsapp.managers.interfaces.OnReadChatCallBack;
import com.softgyan.whatsapp.models.Chats;
import com.softgyan.whatsapp.utils.variables.MessageType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ChatServices {
    private final Context mContext;
    private final String receiverId;
    private final FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

    public ChatServices(Context mContext, String receiverId) {
        this.mContext = mContext;
        this.receiverId = receiverId;
    }

    public void readChats(OnReadChatCallBack callBack) {
        dbReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Chats> chatsList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chats ch = ds.getValue(Chats.class);

                    if (ch != null && ch.getSender().equals(authUser.getUid()) && ch.getReceiver().equals(receiverId)
                            || Objects.requireNonNull(ch).getReceiver().equals(authUser.getUid()) && ch.getSender().equals(receiverId)) {
//                        if (receiverId.equals(ch.getSender()) || authUser.getUid().equals(ch.getSender())) {
                        chatsList.add(ch);
                    }
                }
                callBack.onReadSuccess(chatsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onReadFailed();
            }
        });
    }

    public void sendTextMessage(String message) {
        Chats chats = new Chats(
                getCurrent(),
                message,
                "",
                MessageType.TEXT,
                authUser.getUid(),
                receiverId
        );

        //send message
        dbReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("my_tag", "onSuccess : message sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("my_tag", "onFailure : message failed " + e.getMessage());
            }
        });


        //add to sender chat list
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(authUser.getUid()).child(receiverId);
        chatRef1.child("chat_id").setValue(receiverId);

        //add to receiver chat list
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(receiverId).child(authUser.getUid());
        chatRef2.child("chat_id").setValue(authUser.getUid());

    }

    public void sendImage(String imageUrl) {
        Chats chats = new Chats(
                getCurrent(),
                "",
                imageUrl,
                MessageType.IMAGE,
                authUser.getUid(),
                receiverId
        );

        //send message
        dbReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("my_tag", "onSuccess : message sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("my_tag", "onFailure : message failed " + e.getMessage());
            }
        });


        //add to sender chat list
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(authUser.getUid()).child(receiverId);
        chatRef1.child("chat_id").setValue(receiverId);

        //add to receiver chat list
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(receiverId).child(authUser.getUid());
        chatRef2.child("chat_id").setValue(authUser.getUid());

    }


    public static String getCurrent() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = dateFormat.format(date);
        //todo we are getting error here
//        Calendar currentDateTime = Calendar.getInstance(); //todo time
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentTime = "9:10 am"; //sdf.format(currentDateTime.getTime());
        return today + ", " + currentTime;
    }

    public void sendVoice(String audioPathLocal) {
        final Uri voiceUri = Uri.fromFile(new File(audioPathLocal));
        final StorageReference audioRef = FirebaseStorage
                .getInstance().getReference()
                .child("Chats/Voice/" + System.currentTimeMillis());
        audioRef.putFile(voiceUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        try {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadUri = uriTask.getResult();
                            String voiceUrl = String.valueOf(downloadUri);
                            assert authUser != null;
                            Chats chats = new Chats(
                                    getCurrent(),
                                    "",
                                    voiceUrl,
                                    MessageType.VOICE,
                                    authUser.getUid(),
                                    receiverId
                            );


                            dbReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("my_tag", "onSuccess : message sent");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("my_tag", "onFailure : message failed " + e.getMessage());
                                }
                            });


                            //add to sender chat list
                            DatabaseReference chatRef1 = FirebaseDatabase.getInstance()
                                    .getReference("ChatList")
                                    .child(authUser.getUid()).child(receiverId);
                            chatRef1.child("chat_id").setValue(receiverId);

                            //add to receiver chat list
                            DatabaseReference chatRef2 = FirebaseDatabase.getInstance()
                                    .getReference("ChatList")
                                    .child(receiverId).child(authUser.getUid());
                            chatRef2.child("chat_id").setValue(authUser.getUid());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });


    }

}
