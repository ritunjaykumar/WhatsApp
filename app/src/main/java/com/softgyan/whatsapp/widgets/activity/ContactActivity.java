package com.softgyan.whatsapp.widgets.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.adapter.ContactListsAdapter;
import com.softgyan.whatsapp.databinding.ActivityContactBinding;
import com.softgyan.whatsapp.models.Users;
import com.softgyan.whatsapp.utils.variables.Var;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private ActivityContactBinding mBinding;
    private List<Users> usersList = new ArrayList<>();
    private ContactListsAdapter contactAdapter;
    private FirebaseUser authUser;
    private FirebaseFirestore db;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_contact);
        setSupportActionBar(mBinding.toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("wait for minutes");

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if(authUser!=null){
            getContactList();
        }
    }

    private void getContactList() {
        progress.show();
        db.collection(Var.USERS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot ds : documents){
                        String userId = ds.getString(Var.USER_ID);
                        String userName = ds.getString(Var.USER_NAME);
                        String status = ds.getString(Var.STATUS);
                        String profileUrl = ds.getString(Var.IMAGE_PROFILE);
                        Users  users = new Users();
                        users.setStatus(status);
                        users.setUserId(userId);
                        users.setUserName(userName);
                        users.setImageProfile(profileUrl);
                        if(!userId.equals(authUser.getUid())) {
                            usersList.add(users);
                        }
                    }
                    contactAdapter =new ContactListsAdapter( usersList, ContactActivity.this);
                    mBinding.rvContactList.setAdapter(contactAdapter);
                    contactAdapter.notifyDataSetChanged();
                    progress.dismiss();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContactActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}