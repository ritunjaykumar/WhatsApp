package com.softgyan.whatsapp.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.utils.UserDetails;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            Intent intent;
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            }else{
                if(!UserDetails.getInstance(SplashActivity.this).isUserNameSet()){
                    intent = new Intent(SplashActivity.this, SetUserInfoActivity.class);
                }else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 1000);
    }

}