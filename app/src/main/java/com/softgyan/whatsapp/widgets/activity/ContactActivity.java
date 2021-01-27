package com.softgyan.whatsapp.widgets.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.adapter.ContactListsAdapter;
import com.softgyan.whatsapp.databinding.ActivityContactBinding;
import com.softgyan.whatsapp.models.Contacts;
import com.softgyan.whatsapp.models.Users;
import com.softgyan.whatsapp.utils.variables.Var;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = "my_tag";
    private ActivityContactBinding mBinding;
    private final List<Users> usersList = new ArrayList<>();
    private ContactListsAdapter contactAdapter;
    private FirebaseUser authUser;
    private FirebaseFirestore db;
    private ProgressDialog progress;

    public static final int REQUEST_READ_CONTACTS = 1;
    private ArrayList<Contacts> contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact);
        setSupportActionBar(mBinding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("wait for minutes");

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (authUser != null) {
            getContactsFromPhone();
        }
    }

    private void getContactsFromPhone() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED) {
            getAllContacts();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

    }

    private void getAllContacts() {
//        final ArrayList<Contacts> contactsList = new ArrayList<>();
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
//                null, null, null, null);
//        if ((cursor != null ? cursor.getCount() : 0) > 0) {
//            while (cursor.moveToNext()) {
//                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                String userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//                    Cursor pCur = contentResolver.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
//                            new String[]{id},
//                            null
//                    );
//                    String phoneNumber = "";
//                    while (pCur.moveToNext()) {
//                        phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                    }
//                    StringBuilder sb = new StringBuilder();
//                    for(int i=0; i<phoneNumber.length(); i++){
//                        if(phoneNumber.charAt(i) != ' '){
//                            sb.append(phoneNumber.charAt(i));
//                        }
//                    }
//                    String newPhone = sb.toString();
//
//                    if (!newPhone.startsWith("+91")) {
//                        newPhone = "+91" + newPhone;
//                    }
//                    Log.i(TAG, "getAllContacts: "+newPhone);
//                    contactsList.add(new Contacts(userName, newPhone));
//                    pCur.close();
//                }
//            }
//
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
//        return contactsList;
        GetContactsFromDevices contacts = new GetContactsFromDevices();
        contacts.execute();

    }


    private void getContactList() {
        db.collection(Var.USERS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot ds : documents) {
                        String userId = ds.getString(Var.USER_ID);
                        String userName = ds.getString(Var.USER_NAME);
                        String status = ds.getString(Var.STATUS);
                        String profileUrl = ds.getString(Var.IMAGE_PROFILE);
                        String phone = ds.getString(Var.USER_PHONE);


                        if (!userId.equals(authUser.getUid())) {
                            if ( contactList.size() != 0) {
                                for (Contacts contacts : contactList) {
                                    if (contacts.getNumber().contains(phone)) {
                                        Users users = new Users();
                                        users.setStatus(status);
                                        users.setUserId(userId);
                                        users.setUserName(contacts.getName());
                                        users.setImageProfile(profileUrl);
                                        users.setUserPhone(phone);
                                        usersList.add(users);
                                    }
                                }
                            }
                        }
                    }
                    contactAdapter = new ContactListsAdapter(usersList, ContactActivity.this);
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
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    class GetContactsFromDevices extends AsyncTask<Void, Void ,ArrayList<Contacts>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected ArrayList<Contacts> doInBackground(Void... voids) {
            final ArrayList<Contacts> contactsList = new ArrayList<>();
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if ((cursor != null ? cursor.getCount() : 0) > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                                new String[]{id},
                                null
                        );
                        String phoneNumber = "";
                        while (pCur.moveToNext()) {
                            phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        }
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<phoneNumber.length(); i++){
                            if(phoneNumber.charAt(i) != ' '){
                                sb.append(phoneNumber.charAt(i));
                            }
                        }
                        String newPhone = sb.toString();

                        if (!newPhone.startsWith("+91")) {
                            newPhone = "+91" + newPhone;
                        }
                        contactsList.add(new Contacts(userName, newPhone));
                        pCur.close();
                    }
                }

            }
            if (cursor != null) {
                cursor.close();
            }
            return contactsList;
        }

        @Override
        protected void onPostExecute(ArrayList<Contacts> contacts) {
            super.onPostExecute(contacts);
            contactList = contacts;
            getContactList();
        }
    }
}